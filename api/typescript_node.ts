import { createServer, IncomingMessage, ServerResponse } from "node:http";

type SequenceRequest = {
  values: number[];
};

function findSequence(values: number[]): number[] {
  const n = values.length;
  if (n === 0) return [];

  const lengths = new Array<number>(n).fill(1);
  const parents = new Array<number>(n).fill(-1);
  let bestIndex = 0;

  for (let i = 0; i < n; i++) {
    for (let j = 0; j < i; j++) {
      if (values[j] < values[i] && lengths[j] + 1 >= lengths[i]) {
        lengths[i] = lengths[j] + 1;
        parents[i] = j;
      }
    }

    if (lengths[i] >= lengths[bestIndex]) {
      bestIndex = i;
    }
  }

  const result: number[] = [];
  let current = bestIndex;

  while (current !== -1) {
    result.push(values[current]);
    current = parents[current];
  }

  return result.reverse();
}

function readBody(request: IncomingMessage): Promise<string> {
  return new Promise((resolve, reject) => {
    let body = "";

    request.on("data", (chunk: Buffer) => {
      body += chunk.toString("utf8");
    });

    request.on("end", () => resolve(body));
    request.on("error", reject);
  });
}

function sendJson(response: ServerResponse, status: number, payload: unknown): void {
  const body = JSON.stringify(payload);

  response.writeHead(status, {
    "Content-Type": "application/json; charset=utf-8",
    "Content-Length": Buffer.byteLength(body),
  });

  response.end(body);
}

function parseSequence(body: string): SequenceRequest {
  let parsed: unknown;

  try {
    parsed = JSON.parse(body);
  } catch {
    throw new Error("Invalid JSON");
  }

  const values = (parsed as SequenceRequest).values;

  if (!Array.isArray(values) || values.length === 0) {
    throw new Error("values must be a non-empty array");
  }

  for (const value of values) {
    if (typeof value !== "number" || !Number.isFinite(value)) {
      throw new Error("values must contain finite numbers");
    }
  }

  return { values };
}

const server = createServer(async (request, response) => {
  const url = new URL(request.url ?? "/", "http://localhost");

  if (url.pathname === "/healthz" && request.method === "GET") {
    sendJson(response, 200, { status: "ok" });
    return;
  }

  if (url.pathname === "/api/lis" && request.method === "POST") {
    try {
      const body = await readBody(request);
      const payload = parseSequence(body);
      const result = findSequence(payload.values);
      sendJson(response, 200, { result, length: result.length });
    } catch (error) {
      sendJson(response, 400, {
        error: error instanceof Error ? error.message : "Bad request"
      });
    }
    return;
  }

  sendJson(response, 404, { error: "Not found" });
});

server.listen(8080, () => {
  console.log("Aurelia LIS TypeScript API listening on http://localhost:8080");
});
