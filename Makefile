DATABASE=authentication
TEST_DATABASE=test_authentication

DATABASE_URL=postgres://postgres:password@db:5432/$(DATABASE)?sslmode=disable
TEST_DATABASE_URL=postgres://postgres:password@db:5432/$(TEST_DATABASE)?sslmode=disable

dev/build:
	docker compose -f docker-compose.dev.yml build

dev/build/web:
	docker compose -f docker-compose.dev.yml build web

dev/up:
	docker compose -f docker-compose.dev.yml up -d

dev/down:
	docker compose -f docker-compose.dev.yml down --remove-orphans

dev/ps:
	docker compose -f docker-compose.dev.yml ps

dev/logs/web:
	docker compose -f docker-compose.dev.yml logs -f web

dev/migrate:
	docker compose -f docker-compose.dev.yml run --rm -e DATABASE_URL=$(DATABASE_URL) db_migrate

dev/db/reset:
	docker compose -f docker-compose.dev.yml run --rm -e DATABASE_URL=$(DATABASE_URL) db_migrate drop -f

dev/test/migrate:
	docker compose -f docker-compose.dev.yml run --rm -e DATABASE_URL=$(TEST_DATABASE_URL) db_migrate

dev/test/run:
	docker compose -f docker-compose.dev.yml run --rm test_web sbt test

dev/test/clean:
	docker compose -f docker-compose.dev.yml run --rm -e DATABASE_URL=$(TEST_DATABASE_URL) db_migrate drop -f

dev/test: dev/test/migrate dev/test/run dev/test/clean
