import os
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'backend.settings')
import django  # type: ignore # noqa: E402
from django.contrib.auth import get_user_model  # type: ignore # noqa: E402

ADMIN_USERNAME = "admin"
ADMIN_PASSWORD = "password"
DB_PATH = "./db.sqlite3"
MIGRATIONS_DIR = "./api/migrations"


def reset():
    print("Re-migrating database")

    if os.path.exists(DB_PATH):
        os.remove(DB_PATH)

    if os.path.exists(MIGRATIONS_DIR):
        for item in os.listdir(MIGRATIONS_DIR):
            if os.path.isfile(item):
                os.remove(os.path.join(MIGRATIONS_DIR, item))

    django.setup()

    os.system("python manage.py makemigrations api")
    os.system("python manage.py migrate")

    get_user_model().objects.create_superuser(ADMIN_USERNAME, 'admin@admin.com', ADMIN_PASSWORD)

    print(f"\nRe-migrated with following credentials for the admin account:\n\tusername: {ADMIN_USERNAME}\n\tpassword: {ADMIN_PASSWORD}\n")


if __name__ == "__main__":
    reset()
