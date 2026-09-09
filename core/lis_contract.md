<!-- core/lis_contract.md -->
# LIS Contract

## Purpose

This contract defines the shared behavior for all Aurelia LIS implementations:

- Browser core
- Python FastAPI reference implementation
- TypeScript Node reference implementation
- Java JDK HTTP reference implementation

The contract covers input shape, output shape, error behavior, deterministic tie handling, and verification fixtures.

## Problem Definition

Given a finite numeric sequence, return a longest increasing subsequence.

A valid result must satisfy:

1. Every returned element is drawn from the original input.
2. The returned elements preserve the original input order.
3. The returned elements are strictly increasing.
4. No longer strictly increasing subsequence exists for the same input.

Equal values are not considered increasing. Therefore, `x < y` is required for adjacent selected values.

## API Contract

### Health Endpoint

```http
GET /healthz
