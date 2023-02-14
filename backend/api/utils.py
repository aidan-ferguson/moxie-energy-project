import json
import openai
import os

# Function used to simplify returning JSON structures
def return_json(obj):
    return json.dumps(obj)


def json_error(reason):
    return return_json({"success": False, "reason": reason})


def json_success(return_dict):
    return return_json({"success": True, "data": return_dict})

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
- Avoid using exact number if possible
- The report will be no longer than 100 words
- The tone of the report must be friendly and helpful
        """
        
        # Decide which appliances to include, always include the aggregate reading
        # TODO: proper initial date
        prompt += f"\n\nThe household usage 6 months ago was {energy_usage['initial_usage'][0]:.2f}kWh, the previous week was {energy_usage['previous_week'][0]:.2f}kWh and the last 24 hours it was {energy_usage['today'][0]:.2f}kWh"
        
        # Add appliances which have massive differences in energy usage (i.e. they are interesting)
        
        # End the prompt
        prompt += "Your personal energy usage report:\nHi there,\n"
        return prompt
        
    def get_tipoftheday_prompt():
        prompt = "A brief energy saving fun fact:\n"
        return prompt