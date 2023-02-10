using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LightHealth : UpdatableHealthObject
{
    [SerializeField] Light l;
    float intensity;

    public override void UpdateObject()
    {
        return;
    }

    private void Awake()
    {
        intensity = l.intensity;
    }
    private void Update()
    {
        l.intensity = Mathf.Lerp(0, intensity, healthManager.GetHealth() * healthManager.GetHealth());
        l.color = Color.Lerp(unHealthyColor, healthyColor, healthManager.GetHealth() * healthManager.GetHealth());
    }

}
