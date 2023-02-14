from django.contrib import admin, auth
from api.models import User, Tip, TOTD

class UserAdmin(auth.admin.UserAdmin):
    model = User

    fieldsets = auth.admin.UserAdmin.fieldsets + (
        # n.b. custom attributes go here 
    )

admin.site.register(User, UserAdmin)
admin.site.register(Tip)
admin.site.register(TOTD)