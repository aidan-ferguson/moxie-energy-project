from django.contrib import admin, auth
from api.models import User

class UserAdmin(auth.admin.UserAdmin):
    model = User

    fieldsets = auth.admin.UserAdmin.fieldsets + (
        # n.b. custom attributes go here 
    )

admin.site.register(User, UserAdmin)
