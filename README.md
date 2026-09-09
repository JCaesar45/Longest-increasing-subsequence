# Aurelia LIS

A luxury demonstration product for the longest increasing subsequence problem.

## Product Structure
```
aurelia-lis/
├─ index.html
├─ api/
│  ├─ python_fastapi.py
│  ├─ typescript_node.ts
│  └─ java_lis_server.java
├─ core/
│  └─ lis_contract.md
├─ docs/
│  └─ architecture.md
└─ README.md
```
## Browser
Open index.html in a modern browser. No build step is required.

## Python API
python -m venv .venv
source .venv/bin/activate
pip install fastapi uvicorn
uvicorn python_fastapi:app --reload --port 8000

## TypeScript API
npm install typescript @types/node --save-dev
npx tsc typescript_node.ts --module nodenext --target es2022
node typescript_node.js

## Java API
javac java_lis_server.java
java LisServer

## Endpoint
POST /api/lis
Body: {"values":[3,10,2,1,20]}
Response: {"result":[3,10,20],"length":3}

## References
Aldous, D., & Diaconis, P. (1999). Longest increasing subsequences: from patience sorting to the Baik-Deift-Johansson theorem. Bulletin of the American Mathematical Society, 36(4), 413-432.

Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2022). Introduction to algorithms (4th ed.). MIT Press.

Ecma International. (2024). ECMAScript 2024 language specification. https://tc39.es/ecma262/

Microsoft. (2026). TypeScript documentation. https://www.typescriptlang.org/docs/

Oracle. (2026). Java SE 21 documentation. https://docs.oracle.com/en/java/javase/21/

Python Software Foundation. (2026). Python 3.13 documentation. https://docs.python.org/3/

WHATWG. (2026). HTML Living Standard. https://html.spec.whatwg.org/
