# Jan Wangrat — Project Portfolio

I build LLM-powered products end to end: retrieval and agent pipelines, the FastAPI/Python
services behind them, the React frontends on top, and the infra they run on.

BSc Computer Science (2024) and BSc Mathematics (2025), University of Warsaw.
MSc Computer Science and Engineering at Politecnico di Milano (expected 2027).

- **Email**: [jan@wangrat.com](mailto:jan@wangrat.com)
- **LinkedIn**: [jan-wangrat](https://www.linkedin.com/in/jan-wangrat-b2b651236/)
- **Location**: Milan / Warsaw

## Where I work

| | Role |
| --- | --- |
| **[Neural Alpha](https://neuralalpha.com)** | AI Software Developer — building an LLM-powered ESG intelligence platform on Python, Neo4j, MongoDB, LangChain and AWS |
| **[TaxCompass](https://taxcompass.it)** | Founder (2026) |
| **RewAIre** | Founder (2026) |

Work done at Neural Alpha belongs to Neural Alpha and is not published here.

---

## Research & theses

| Year | Project | What it is | Stack |
| --- | --- | --- | --- |
| 2025 | [**Unbalanced Optimal Transport for NMR mixture analysis**](bachelor-thesis-MirrorDescent) — BSc Mathematics thesis | A scalable mirror-descent method for estimating spectral components of NMR mixtures via unbalanced optimal transport. Experiment code: [`janwan2003/wasserstein`](https://github.com/janwan2003/wasserstein) | Python, optimal transport |
| 2024 | [**DucklingLS**](bachelor-thesis-DucklingLS) — BSc Computer Science thesis | Language-server support for a new programming language: LSP design, incremental analysis, editor integration | TypeScript, LSP |

---

## Recent work (2025–2026)

Larger projects live in their own repositories.

| Year | Project | What it is | Stack | Links |
| --- | --- | --- | --- | --- |
| 2026 | **TaxCompass** | AI tax co-pilot for foreign founders setting up a business in Italy. Multi-agent RAG over primary law (Normattiva, Agenzia delle Entrate, INPS) with inline citations, a deterministic multi-country tax engine, and a prerendered SEO marketing site. Shipped to production. | LangGraph, FastAPI, pgvector, Azure OpenAI, React, Terraform | [taxcompass.it](https://taxcompass.it) · repo private |
| 2026 | **RewAIre** | Three-layer pipeline (Observe → Judge → Recommend) that turns workflow evidence into an AI-transformation blueprint, explorable as an interactive workflow graph. Run as a Politecnico di Milano capstone with three students owning one layer each, integrating over a shared Pydantic schema. | FastAPI, MongoDB/GridFS, Pydantic, React Flow | repo private |
| 2025–26 | **Intelligent Job Management** | Scheduler for GPU deep-learning clusters: profiling-based placement, stoppable/resumable jobs, multi-node support. Modelled after Polimi's ANDREAS project. | Python, Docker, distributed systems | [repo](https://github.com/janwan2003/intelligent-job-management) |
| 2025–26 | **Embedding Model Selection Platform** | Benchmark and pick embedding models against your own data, deployed on Azure. | TypeScript, Python, Azure | [repo](https://github.com/EmbedMatch/embedding-project-cloud) |
| 2026 | **Exam Prep** | Study app over 51 past Polimi exams — spaced repetition, exam simulation, topic analytics — behind a Zod-validated content pipeline. | React, TypeScript, Vite, Azure | [live](https://ml-exam-prep-jw.azurewebsites.net) · repo private |
| 2026 | **Reply Mirror** | Fraud-detection multi-agent system for the Reply AI Agent Challenge: four cooperating LLM agents (phishing, geolocation, transaction profiling, adjudication) on a LangGraph pipeline. | LangGraph, Python | repo private |
| 2026 | **LLM Calculation Benchmark** | 500 calculation problems with tool-verified ground truth (SymPy / `Fraction`), plus a pipeline that pulls real olympiad problems from published PDFs and an async evaluation notebook. Gemini 2.5 Flash scores 83.4% — and fails on applied multi-step problems, not on the ones labelled hard. | Python, SymPy, LangChain, MongoDB/JSON | [repo](https://github.com/janwan2003/llm-calculation-benchmark) |
| 2026 | **Sandbox Party** | Jackbox-style party game with an LLM game master: authoritative Colyseus server drives the phase machine and the model, a Godot 4.6 screen renders, and everyone plays from their phone over the LAN. | Node.js, Colyseus, Godot/GDScript, Gemini | [repo](https://github.com/janwan2003/sandbox-party) |
| 2026 | **PokerChips** | Local-network poker chip manager: Android app with an embedded web server, no internet required. | Kotlin, Android | [repo](https://github.com/janwan2003/pokerchips) |
| 2026 | **Exploding Kittens on your LAN** | Self-hosted card game for 2–10 players: authoritative Socket.IO server owns all hidden state, shared TypeScript types make a protocol break a compile error, 108 tests over the rules engine (Nope-on-Nope stacking included). | TypeScript, Socket.IO, React | [repo](https://github.com/janwan2003/exploding-kittens-lan) |
| 2026 | **WeGoWhen** | Collaborative trip planner — group availability heat map, shareable links, optional Supabase sync. | React, TypeScript, Supabase | [live](https://janwan2003.github.io/trip-planner/) · [repo](https://github.com/janwan2003/trip-planner) |
| 2024 | **Goldman Sachs hackathon** | Calendar-integrated scheduling service built in 24h. | TypeScript, Python | [frontend](https://github.com/goldman-hackathon/goldman-front) · [backend](https://github.com/goldman-hackathon/goldman-back) |

---

## University of Warsaw coursework (2021–2024)

Kept for the record — course assignments from the CS and Mathematics degrees, not current
work. Stars are my own take on how interesting each one is.

<details>
<summary>Show 15 projects</summary>

| Year | Project | What it is | Course / stack | |
| --- | --- | --- | --- | --- |
| 2023/24 | [python-buses-project](python-buses-project) | Warsaw bus network simulation and analysis | Python, Dagster | ★★★★★ |
| 2023/24 | [interpreter-janeklang](interpreter-janeklang) | Interpreter for a custom language, with editor tooling | Haskell, Programming Languages and Tools | ★★★★★ |
| 2023/24 | [deep-neural-networks](deep-neural-networks) | Deep Neural Networks course assignments | Python, PyTorch | ★★★★★ |
| 2022/23 | [executor](executor) | Concurrent task execution and management utility | C, Parallel Programming | ★★★★☆ |
| 2022/23 | [internet-radio](internet-radio) | Internet radio sender/receiver with retransmission | C++, Java, Computer Networks | ★★★★☆ |
| 2022/23 | [nice-graph-drawing-cpp](nice-graph-drawing-cpp) | Graph layout and drawing | C++, LaTeX, Software Engineering | ★★★★☆ |
| 2022/23 | [c-program-compiler-web-aplication](c-program-compiler-web-aplication) | Web front end for compiling and running C programs | Python, Django, WWW Applications | ★★★☆☆ |
| 2021/22 | [java-matrices-operations](java-matrices-operations) | Matrix operations library | Java, Object-Oriented Programming | ★★★☆☆ |
| 2022/23 | [java-workers-factory](java-workers-factory) | Worker/task assignment simulation | Java, Parallel Programming | ★★★☆☆ |
| 2021/22 | [bajt-trade-java](bajt-trade-java) | Multi-agent trading simulation | Java, Object-Oriented Programming | ★★☆☆☆ |
| 2022/23 | [distributed-stack-machine](distributed-stack-machine) | Stack machine across distributed nodes | Assembly, Operating Systems | ★★☆☆☆ |
| 2022/23 | [inverting-permutation](inverting-permutation) | In-place permutation inversion | Assembly, Operating Systems | ★★☆☆☆ |
| 2023/24 | [prolog-program-verifier](prolog-program-verifier) | Verifier for a small imperative language | Prolog, Programming Languages and Tools | ★★☆☆☆ |
| 2021/22 | [ski-jumping-website](ski-jumping-website) | Ski jumping contest management site | PHP, PostgreSQL, Databases | ★☆☆☆☆ |
| 2023/24 | [haskell-graph-and-set](haskell-graph-and-set) | Functional set and graph manipulation | Haskell, Programming Languages and Tools | ★☆☆☆☆ |

</details>

---

## Stack

**Languages** — Python, TypeScript, Java, C/C++, Haskell, Kotlin, SQL, Assembly

**AI / data** — LangGraph, LangChain, RAG and hybrid retrieval, pgvector, Qdrant, Neo4j,
PyTorch, Azure OpenAI, Anthropic API

**Backend** — FastAPI, SQLAlchemy, Pydantic, MongoDB, PostgreSQL, MySQL, Django

**Frontend** — React, Vite, Tailwind, shadcn/ui, TanStack Query

**Infra** — Docker, Terraform, AWS, Azure, GitHub Actions
