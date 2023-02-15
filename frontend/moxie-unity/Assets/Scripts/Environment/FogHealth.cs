using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class FogHealth : UpdatableHealthObject
{
    [SerializeField]
    LightingManager lightingManager;

    public override void UpdateObject()
    {
        return;
    }

    private void Update()
    {
        float colorLerp = Mathf.Clamp(healthManager.GetHealth() * 1f, 0, 1);
        RenderSettings.fogColor = Color.Lerp(lightingManager.GetFogColor(), Color.Lerp(unHealthyColor, healthyColor, colorLerp), 0f);
    }
}
