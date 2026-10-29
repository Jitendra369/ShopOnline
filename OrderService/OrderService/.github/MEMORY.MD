# Feature notes

## Order and OrderItem CRUD service (2026-10-10)
- Added `Order` and `OrderItem` JPA entities. Both inherit UUID `id`, `createdAt`, and `updatedAt` from `AbstractBaseEntity`.
- Orders receive a generated `ORD-...` business identifier, default to `CREATED`, and keep their items through cascade and orphan removal.
- Added repositories, request/response DTOs, service interfaces and implementations, REST controllers, and custom API exception handling.
- Added order CRUD at `/api/orders` and item CRUD at `/api/orders/{orderId}/items` plus `/api/order-items/{itemId}`.
- Order totals are recalculated from each item's quantity and unit price after item changes.
- Database connection and schema settings remain environment/deployment configuration; `application.properties` currently only sets the application name.
