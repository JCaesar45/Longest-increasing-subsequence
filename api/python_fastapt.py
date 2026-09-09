# aurelia_lis.py
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field, field_validator
from typing import List
import math

app = FastAPI(
    title="Aurelia LIS API",
    description="Luxury-grade longest increasing subsequence endpoint.",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["GET", "POST", "OPTIONS"],
    allow_headers=["*"],
)


class SequenceRequest(BaseModel):
    values: List[float] = Field(..., min_length=1)

    @field_validator("values")
    @classmethod
    def validate_values(cls, values: List[float]) -> List[float]:
        if any(not math.isfinite(value) for value in values):
            raise ValueError("values must contain finite numbers")
        return values


def find_sequence(values: List[float]) -> List[float]:
    n = len(values)
    if n == 0:
        return []

    lengths = [1] * n
    parents = [-1] * n
    best_index = 0

    for i in range(n):
        for j in range(i):
            if values[j] < values[i] and lengths[j] + 1 >= lengths[i]:
                lengths[i] = lengths[j] + 1
                parents[i] = j

        if lengths[i] >= lengths[best_index]:
            best_index = i

    result = []
    current = best_index
    while current != -1:
        result.append(values[current])
        current = parents[current]

    return result[::-1]


@app.get("/healthz")
def healthz() -> dict:
    return {"status": "ok"}


@app.post("/api/lis")
def lis(payload: SequenceRequest) -> dict:
    result = find_sequence(payload.values)
    return {"result": result, "length": len(result)}
