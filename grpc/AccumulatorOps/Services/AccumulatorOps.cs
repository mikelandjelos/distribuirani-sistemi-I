using Grpc.Core;
using Google.Protobuf.WellKnownTypes;
using Microsoft.AspNetCore.Mvc;

namespace AccumulatorOps.Services;

public class AccumulatorOpsService : AccumulatorOps.AccumulatorOpsBase
{
    private readonly ILogger<AccumulatorOpsService> _logger;

    private readonly object _lockObj = new();

    private static int _accumulatorValue;

    // Staticki konstruktor, poziva se jednom za vreme celog izvrsenja programa.
    // Sluzi za inicijalizaciju statickih promenljivih.
    static AccumulatorOpsService()
    {
        _accumulatorValue = 0;
    }

    public AccumulatorOpsService(ILogger<AccumulatorOpsService> logger)
    {
        _logger = logger;
    }

    // Ne obracati mnogo paznje na ovu funkciju, tu je samo zbog thread safety-ja
    private int PerformOperation(OperationType type, int? operand)
    {
        int valueAfterOperation;

        lock (_lockObj)
        {
            switch (type)
            {
                case OperationType.Nop:
                    break;
                case OperationType.Inc:
                    ++_accumulatorValue;
                    break;
                case OperationType.Dec:
                    --_accumulatorValue;
                    break;
                case OperationType.Add:
                    _accumulatorValue += operand
                        ?? throw new ArgumentNullException("Cannot perform ADD without second operand!");
                    break;
                default:
                    throw new ArgumentOutOfRangeException($"No operation of type {type} supported.");
            }

            valueAfterOperation = _accumulatorValue;
        }

        _logger.LogInformation($"Value after operation: {valueAfterOperation}.");

        return valueAfterOperation;
    }

    public override Task<StringValue> Ping(Empty request, ServerCallContext context)
    {
        return Task.FromResult(new StringValue { Value = "Pong" });
    }

    public override Task<Int32Value> GetAccumulatorValue(Empty request, ServerCallContext context)
    {
        return Task.FromResult(new Int32Value { Value = _accumulatorValue });
    }

    public override Task<PerformOperationResponse> PerformOperationUnary(Operation request, ServerCallContext context)
    {
        int valueAfterOperation = PerformOperation(request.Type, request.Operand);

        _logger.LogInformation($"Value after operation: {valueAfterOperation}.");

        return Task.FromResult(new PerformOperationResponse
        {
            AccumulatorValue = valueAfterOperation,
            Message = $"Successfully performed {request.Type} operation."
        });
    }

    public override async Task<PerformOperationResponse> PerformOperationClientStream(IAsyncStreamReader<Operation> requestStream, ServerCallContext context)
    {
        int? lastOperationValue = null;
        int numberOfOperations = 0;

        while (await requestStream.MoveNext() && !context.CancellationToken.IsCancellationRequested)
        {
            Operation operation = requestStream.Current;

            if (operation.Type == OperationType.Nop)
                break;

            lastOperationValue = PerformOperation(operation.Type, operation.HasOperand ? operation.Operand : null);

            _logger.LogInformation($"Value after operation: {lastOperationValue}.");
            
            numberOfOperations++;
        }        

        return new PerformOperationResponse
        {
            AccumulatorValue = lastOperationValue ?? _accumulatorValue,
            Message = $"Succesfully performed {numberOfOperations} operations."
        };
    }

    public override async Task PerformOperationBiStream(IAsyncStreamReader<Operation> requestStream, IServerStreamWriter<PerformOperationResponse> responseStream, ServerCallContext context)
    {
        int operationValue;
        int numberOfOperations = 0;

        while (!context.CancellationToken.IsCancellationRequested && await requestStream.MoveNext())
        {
            Operation operation = requestStream.Current;

            if (operation.Type == OperationType.Nop)
                break;

            operationValue = PerformOperation(operation.Type, operation.HasOperand ? operation.Operand : null);

            _logger.LogInformation($"Value after operation: {operationValue}.");

            numberOfOperations++;

            await responseStream.WriteAsync(new PerformOperationResponse
            {
                AccumulatorValue = operationValue,
                Message = $"Succesfully performed {numberOfOperations + 1} operation."
            });
        }
    }
}
