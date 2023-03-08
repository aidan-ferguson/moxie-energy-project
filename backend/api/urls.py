from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token
from . import views

urlpatterns = [
    path('test-connection', views.TestView.as_view(), name='test-connection'),
    path('auth/register', views.RegisterView.as_view(), name='register'),
    path('auth/logout', views.LogoutView.as_view(), name='logout'),
    path('usage/appliances', views.AppliancesView.as_view(), name='get-appliances'),
    path('usage/national-average', views.NationalAverageView.as_view(), name='national-average'),
    path('auth/get-token', obtain_auth_token, name='token_auth'),
    path('user/information', views.UserInfoView.as_view(), name='user-information'),
    path('tips/totd', views.TOTDView.as_view(), name="tip-of-the-day"),
    path('tips/energy-report', views.EnergyReportView.as_view(), name='energy-report'),
    path('tips/appliance', views.ApplianceTips.as_view(), name="appliance-tips"),
    path('user/friends', views.FriendView.as_view(), name="friends"),
    path('usage/available-data-providers', views.DataProviderView.as_view(), name="list-data-providers"),
    path('user/delete-account', views.DeleteAccountView.as_view(), name="delete-account")
]
