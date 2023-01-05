using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LightHealth : MonoBehaviour
{
    [SerializeField] Light l;
    float intensity;

    [SerializeField] Color healthyColor;
    [SerializeField] Color unHealthyColor;

    private void Awake()
    {
        intensity = l.intensity;
    }
    private void Update()
    {
        l.intensity = Mathf.Lerp(0, intensity, HealthManager.Instance().GetHealth() * HealthManager.Instance().GetHealth());
        l.color = Color.Lerp(unHealthyColor, healthyColor, HealthManager.Instance().GetHealth() * HealthManager.Instance().GetHealth());
    }

}
