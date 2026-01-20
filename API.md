# Lab 10 – HTTP Fundamentals API

Base URL: http://localhost:8080

## 1) Inspect request (headers + query params)
- Method: GET
- Path: /api/inspect
- Query params: q (optional)
- Headers: X-Request-Id (optional)
- Returns:
    - 200 OK (application/json) -> { method, path, q, userAgent, requestId }

## 2) JSON body parsing + validation
- Method: POST
- Path: /api/users
- Content-Type: application/json
- Body: { username, email, password }
- Returns:
    - 201 Created -> { id, username, email }
    - 400 Bad Request -> structured validation error JSON
    - 415 Unsupported Media Type -> when Content-Type is not application/json

## 3) Form-data parsing (x-www-form-urlencoded) + validation
- Method: POST
- Path: /api/login-form
- Content-Type: application/x-www-form-urlencoded
- Form fields: email, password
- Returns:
    - 200 OK -> { message: "OK", userId }
    - 400 Bad Request -> structured error JSON

## 4) Multipart upload
- Method: POST
- Path: /api/upload
- Content-Type: multipart/form-data
- Parts:
    - file (required)
    - description (optional)
- Returns:
    - 200 OK -> { filename, size, description }
    - 400 Bad Request -> structured error JSON
    - 415 Unsupported Media Type -> wrong Content-Type
