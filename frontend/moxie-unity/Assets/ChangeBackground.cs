using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ChangeBackground : MonoBehaviour
{
    public Camera cam;

    // Start is called before the first frame update
    void Start()
    {
        cam = GetComponent<Camera>();
        cam.clearFlags = CameraClearFlags.SolidColor;
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    void change(string colour)
    {
        string[] colour_components = colour.Split(",");
        cam.backgroundColor = new Color(int.Parse(colour_components[0])/255.0f, int.Parse(colour_components[1])/255.0f, int.Parse(colour_components[2])/255.0f);
        cam.backgroundColor = new Color(1.0f, 1.0f, 1.0f);
    }
}
