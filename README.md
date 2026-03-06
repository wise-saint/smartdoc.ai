<h1 align="center">smartdoc.ai</h1>

smartdoc.ai is a **Retrieval-Augmented Generation (RAG) system** that enables users to upload documents and query them through a conversational interface. 
The system retrieves relevant document context and generates grounded responses while maintaining **multi-turn chat history**.

## Document Ingestion Pipeline

```mermaid
flowchart TD
    A[Upload Document]
    B[Text Extraction + Preprocessing]
    C[Chunking]
    D[Generate Embeddings]
    E[Upsert Vectors in Qdrant]
    F[Store Chunks in MongoDB]

    A --> B
    B --> C
    C --> D
    D --> E
    C --> F
```

## Retrieval Pipeline
```mermaid
flowchart TD
    A[User Query]
    B[Query Preprocessing]
    C[Hybrid Retrieval]
    D[Vector Search]
    E[MongoDB BM25 Search]
    F[Reciprocal Rank Fusion]
    G[Candidate Chunks]
    H[Cohere Rerank]
    I[LLM Response]

    A --> B
    B --> C
    C --> D
    C --> E
    D --> F
    E --> F
    F --> G
    G --> H
    H --> I
```

## Tech Stack

| Layer | Technology |
|------|-------------|
| Backend | Java, Spring Boot |
| Database | MongoDB |
| Vector Database | Qdrant |
| Cache | Redis |
| Embedding Model | BGE (BAAI General Embedding) via HuggingFace |
| LLM | SmolLM3B |
| Reranking | Cohere Reranker |
| Document Processing | Apache PDFBox, Apache POI |
| Architecture |Port and Adapter (Hexagonal Architecture) |
