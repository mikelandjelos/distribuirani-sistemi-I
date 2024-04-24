using Grpc.Net.Client;
using static AccumulatorOps.AccumulatorOps;
using AccumulatorOps;
using Google.Protobuf.WellKnownTypes;
using System.Reflection;
using Grpc.Core;

// The port number must match the port of the gRPC server.
using var channel = GrpcChannel.ForAddress("https://localhost:7112");
var accumulatorClientStub = new AccumulatorOpsClient(channel);

# region Tests

async Task<bool> TestPing()
{
    var ping = await accumulatorClientStub.PingAsync(new Empty());
    return ping.Value == "Pong";
}


// Svi testovi su pisani sa pretpostavkom da nema poziva od strane drugih klijenata za vreme njihovog trajanja.
// Ukoliko se ipak tako nesto i desi, testovi nece nece proci (vratice `false`).

async Task<bool> TestPeformOperationUnary()
{
    int beforeTestingValue = (await accumulatorClientStub.GetAccumulatorValueAsync(new Empty())).Value;

    var afterIncrement = await accumulatorClientStub.PerformOperationUnaryAsync(new Operation
    {
        Type = OperationType.Inc
    });

    if (afterIncrement.AccumulatorValue != beforeTestingValue + 1)
        return false;

    var afterDecrement = await accumulatorClientStub.PerformOperationUnaryAsync(new Operation
    {
        Type = OperationType.Dec
    });

    if (afterDecrement.AccumulatorValue != beforeTestingValue)
        return false;

    var afterAdding = await accumulatorClientStub.PerformOperationUnaryAsync(new Operation
    {
        Type = OperationType.Add,
        Operand = 10,
    });

    if (afterAdding.AccumulatorValue != beforeTestingValue + 10)
        return false;

    var afterSubtracting = await accumulatorClientStub.PerformOperationUnaryAsync(new Operation
    {
        Type = OperationType.Add,
        Operand = -10,
    });

    if (afterSubtracting.AccumulatorValue != beforeTestingValue)
        return false;

    return true;
}

async Task<bool> TestPerformOperationClientStream()
{
    int beforeTestingValue = (await accumulatorClientStub.GetAccumulatorValueAsync(new Empty())).Value;

    var simplex = accumulatorClientStub.PerformOperationClientStream();

    await simplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Inc
    });

    await simplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Dec,
    });

    await simplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Add,
        Operand = 10,
    });

    await simplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Add,
        Operand = -10,
    });

    // Used to signal the end of streaming.
    await simplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Nop
    });

    var afterTestingResponse = await simplex.ResponseAsync;

    Console.WriteLine(afterTestingResponse);

    if (beforeTestingValue != afterTestingResponse.AccumulatorValue)
        return false;

    return true;
}

async Task<bool> TestPerformOperationBiStream()
{
    int beforeTestingValue = (await accumulatorClientStub.GetAccumulatorValueAsync(new Empty())).Value;

    var duplex = accumulatorClientStub.PerformOperationBiStream();

    var accumulatorValues = new int[] { beforeTestingValue + 1, beforeTestingValue, beforeTestingValue + 10, beforeTestingValue };

    await duplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Inc
    });

    await duplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Dec,
    });

    await duplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Add,
        Operand = 10,
    });

    await duplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Add,
        Operand = -10,
    });

    // Used to signal the end of streaming.
    await duplex.RequestStream.WriteAsync(new Operation
    {
        Type = OperationType.Nop
    });

    int i = 0;
    await foreach (var response in duplex.ResponseStream.ReadAllAsync())
    {
        if (response.AccumulatorValue != accumulatorValues[i++])
            return false;
    }

    return true;
}

var tests = new List<Func<Task<bool>>>
{
    TestPing,
    TestPeformOperationUnary,
    TestPerformOperationClientStream,
    TestPerformOperationBiStream,
};

#endregion Tests

#region Run tests

foreach (var test in tests)
    Console.WriteLine($"Tested '{test.GetMethodInfo().Name}'. Success: {await test()}");

#endregion Run tests

Console.WriteLine("Press any key to exit...");
Console.ReadKey();