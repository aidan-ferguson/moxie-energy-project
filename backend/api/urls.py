from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token
from . import views

urlpatterns = [
    path('test-connection', views.TestView.as_view(), name='test-connection'),
    path('auth/register', views.RegisterView.as_view(), name='register'),
    path('auth/logout', views.LogoutView.as_view(), name='logout'),
    path('usage/appliances', views.AppliancesView.as_view(), name='get_appliances'),
    path('usage/national-average', views.NationalAverageView.as_view(), name='national-average'),
    path('auth/get-token', obtain_auth_token, name='token_auth'),
    path('user/information', views.UserInfoView.as_view(), name='user-information'),
    path('tips/totd', views.TOTDView.as_view(), name="daily-tip"),
    path('tips/energy-report', views.EnergyReportView.as_view(), name='energy-report'),
]
