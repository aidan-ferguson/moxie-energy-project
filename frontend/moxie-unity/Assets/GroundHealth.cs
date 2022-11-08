using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GroundHealth : Updatable
{
    [SerializeField]
    Material crackMat;

    [SerializeField]
    Material healthyMat;

    [SerializeField]
    Color healthyColor;

    [SerializeField]
    Color unhealthyColor;

    public override void UpdateObject()
    {
        Vector2 tiling = Vector2.one * (1- HealthManager.Instance().GetHealth()) * 10000;
        crackMat.mainTextureScale = tiling;

        float colorLerp = Mathf.Clamp(HealthManager.Instance().GetHealth() * 1f, 0, 1);
        healthyMat.SetColor("_BaseColor", Color.Lerp(unhealthyColor, healthyColor, colorLerp));
    }
}
