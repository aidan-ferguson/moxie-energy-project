import openai
import os
from api.data_providers.dale_data_provider import DALEDataProvider
import numpy as np
import math

ENERGY_SIGMOID_GRADIANT_STEEPNESS = 0.4
ENERGY_NORMALISATION_SIGMOID_STRECH = 0.2


# Function used to simplify returning JSON structures
def json_error(reason):
    return {"success": False, "reason": reason}


def json_success(return_dict):
    return {"success": True, "data": return_dict}


# Used for querying OpenAI GPT3
def prompt_gpt3(prompt):
    # Get the list of tips from OpenAI
    openai.api_key = os.getenv("OPENAI_API_KEY")
    response = openai.Completion.create(
        model="text-davinci-003",
        prompt=prompt,
        temperature=0.7,
        max_tokens=200,
        top_p=1,
        frequency_penalty=0,
        presence_penalty=0
    )
    
    if len(response["choices"]) == 0:
        print(response)
        raise openai.APIError("Could not generate energy saving advice")
    
    return response["choices"][0]["text"]


# Class that will hold prompt templates for prompting gpt3
class Prompts:
    def get_energy_report_prompt(energy_usage):
        prompt = """
- You are a friendly energy saving advisor AI that will generate a personal energy report for the user
- This report will include a brief overview of the energy usage and one or two actionable and attainable energy saving tips.
- Keep it breif, easy to understand and personalised.
- The report should not contain any specific percentages, instead it will use natural language. For example 10% less energy usage could be 'a little less energy' and 40% more energy usage could be 'significantly more energy'
- Avoid using exact numbers if possible
- The report will be no longer than 100 words
- The tone of the report must be friendly and helpful
        """
        
        # Decide which appliances to include, always include the aggregate reading
        prompt += f"\n\nThe household usage 6 months ago was {energy_usage['initial_usage'][0]:.2f}kWh, the previous week was {energy_usage['previous_week'][0]:.2f}kWh and the last 24 hours it was {energy_usage['today'][0]:.2f}kWh"
        
        # Add appliances which have massive differences in energy usage (i.e. they are interesting)
        
        # End the prompt
        prompt += "Your personal energy usage report:\nHi there,\n"
        return prompt
        
    def get_tipoftheday_prompt():
        prompt = "A brief energy saving fun fact:\n"
        return prompt
    
    def get_device_tip_prompt(energy_usage, device):
        prompt = """
- You are a friendly energy saving advisor AI that will generate a couple of helpful and actionable energy saving tips
- The tips will breif and easy to understand
- The report should not contain any specific percentages, instead it will use natural language. For example 10% less energy usage could be 'a little less energy' and 40% more energy usage could be 'significantly more energy'
- Avoid using exact numbers if possible
- The report will be no longer than 50 words
- The tone of the report must be friendly and helpful
        """
        device_idx = energy_usage["labels"].index(device)
        prompt += f"The device is a {device}"
        prompt += f"The device used up {energy_usage['initial_usage'][device_idx]:.2f}kWh on average 6 months ago, the previous week was {energy_usage['previous_week'][device_idx]:.2f}kWh and the last 24 hours it was {energy_usage['today'][device_idx]:.2f}kWh"
        return prompt
    
        
# Rules to choose which data provider to use depending on the users preference
def get_user_energy_data(user):
    # DALE dataset
    if user.data_provider.startswith("DALE"):
        # user.data_provider should now be in format DALE:house_n
        house = user.data_provider.split(":")[1]
        return DALEDataProvider.get_energy_data(house)


def sigmoid(x):
    return 1/(1+np.exp(-x * ENERGY_SIGMOID_GRADIANT_STEEPNESS))


# Will return a normalised energy rating in the range (0, 1)
# https://www.desmos.com/calculator/mifnjmnoy4
def normalise_energy_rating(rating):
    a = ENERGY_NORMALISATION_SIGMOID_STRECH
    return sigmoid((1/a) * (rating - (a * 5)))


def calculate_energy_score(data):
    score = normalise_energy_rating((data["previous_week"][0]/data["today"][0]))
    if (math.isnan(score) or math.isinf(score)):
        score = 0.0
    return score
