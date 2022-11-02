using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class FogHealth : MonoBehaviour
{
    [SerializeField]
    LightingManager lightingManager;
    [SerializeField]
    Color healthyColor;

    [SerializeField]
    Color unhealthyColor;

    private void Update()
    {
        float colorLerp = Mathf.Clamp(HealthManager.Instance().GetHealth() * 1f, 0, 1);
        RenderSettings.fogColor = Color.Lerp(lightingManager.GetFogColor(), Color.Lerp(unhealthyColor, healthyColor, colorLerp), 0f);
    }
}
