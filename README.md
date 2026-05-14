# Sample OpenID Connect Server

A minimal Spring Boot application that acts as an OpenID Connect issuer. It exposes:

| Endpoint | Description |
|---|---|
| `GET /.well-known/openid-configuration` | OpenID Connect discovery document |
| `GET /jwks` | JSON Web Key Set (RSA public key) |
| `GET /token/generate` | Generate a JWT with default claims |
| `POST /token/generate` | Generate a JWT with custom user claims |

Tokens are signed with **RS256** (RSA-2048). A fresh key pair is generated at startup.

---

## Running locally

```bash
# Build
mvn clean package -DskipTests -s settings-central.xml

# Run
java -jar target/sample-openid-0.0.1-SNAPSHOT.jar
```

The server starts on port **8080** (HTTP only).

---

## Authentication

The `/token/generate` endpoint is protected with **HTTP Basic Auth**.  
The OIDC discovery (`/.well-known/openid-configuration`) and JWKS (`/jwks`) endpoints are public.

Default credentials (override with env vars):

| Env var | Default |
|---|---|
| `TOKEN_USERNAME` | `admin` |
| `TOKEN_PASSWORD` | `changeme` |

---

## Token generation

### Default user (GET)
```bash
curl -u admin:changeme http://localhost:8080/token/generate
```

### Custom user (POST)
All fields are optional — omitted fields fall back to defaults.

```bash
curl -u admin:changeme -X POST http://localhost:8080/token/generate \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "givenName": "John",
    "familyName": "Doe",
    "userId": "abc-123",
    "sub": "custom-sub-id",
    "buId": "some-bu-id",
    "audience": "my-client-id",
    "clientId": "my-client-id",
    "groups": ["Admin", "Developer"]
  }'
```

Tokens always have **1 hour** validity (`iat` = now, `exp` = now + 3600s).

---

## Cloud deployment (Render.com free tier)

1. Push this repo to GitHub.
2. Create a new **Web Service** on [Render](https://render.com).
3. Set the environment variable `ISSUER_URL` to your Render URL, e.g. `https://sample-openid.onrender.com`.
4. Use the provided `render.yaml` for automatic configuration.

> **Note:** The app supports HTTP only. On Render free tier, HTTPS is handled by Render's proxy — the app itself listens on plain HTTP.

---

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `PORT` | `8080` | HTTP port |
| `ISSUER_URL` | `http://localhost:8080` | Base URL used in `iss` claim and discovery doc |
| `TOKEN_USERNAME` | `admin` | Basic Auth username for `/token/generate` |
| `TOKEN_PASSWORD` | `changeme` | Basic Auth password for `/token/generate` |
