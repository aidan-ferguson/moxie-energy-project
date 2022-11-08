using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using UnityEngine;
using Color = UnityEngine.Color;

public class ColorHealth : MonoBehaviour
{
    [SerializeField] Color healthyColor;
    [SerializeField] Color unHealthyColor;

    [SerializeField] float emmision;

    [SerializeField] Material material;

    private void Awake()
    {
        healthyColor = healthyColor * emmision;
    }

    // Update is called once per frame
    void Update()
    {
        float lerpV = HealthManager.Instance().GetHealth();
        material.SetColor("_EmissionColor", Color.Lerp(unHealthyColor, healthyColor, lerpV * lerpV));
    }
}
