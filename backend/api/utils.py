from django.http import HttpResponse
import json


# Function used to simplify returning JSON structures
def return_json(obj):
    return HttpResponse(json.dumps(obj))


def return_error(reason):
    return return_json({"success": False, "reason": reason})


def return_success(return_dict):
    return return_json({"success": True, "data": return_dict})
