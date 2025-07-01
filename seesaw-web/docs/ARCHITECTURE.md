# 시스템 아키텍처 구성도

이 문서는 시스템의 아키텍처 구성도를 설명합니다.

## 네트워크 구성도

아래 다이어그램은 시스템의 네트워크 구성을 보여줍니다.

```mermaid
graph TD
    A[Internet] -->|HTTP, HTTPS| B[Web Server]
    B -->|SSH Tunnel, Reverse Proxy | C[WAS]
    B -->|SSH Tunnel| D[DB]
    style C fill: #ED8B00, stroke: #ED8B00, color: #fff
    style D fill: #336791, stroke: #336791, color: #fff
```

## 구성 요소 설명

### Internet

- 외부 사용자 접근 지점

### Web Server

- 역할: 정적 콘텐츠 제공 및 WAS로 요청 전달
- 애플리케이션:
  - ![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white)

### WAS (Web Application Server)

- 역할: 애플리케이션 로직 처리 및 동적 콘텐츠 생성
- 애플리케이션:
  - `seesaw-web` ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
  - `seesaw-api` ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
  - `seesaw-console` ![Node.js](https://img.shields.io/badge/Node.js-339933?style=flat&logo=nodedotjs&logoColor=white)

### DB (Database Server)

- 역할: 데이터 저장 및 관리
- 애플리케이션:
  - ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
  - ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=flat&logo=postgresql&logoColor=white)

## 보안 고려사항

- 모든 서버 간 통신은 SSH 터널을 통해 암호화됩니다.
- 직접적인 인터넷에서 WAS나 DB로의 접근은 불가능합니다.
- Web Server만 외부 인터넷에 노출되어 있습니다.
