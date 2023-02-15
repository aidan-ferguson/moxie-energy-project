using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GroundHealth : UpdatableHealthObject
{
    [SerializeField]
    Material crackMat;

    [SerializeField]
    Material healthyMat;

    public override void UpdateObject()
    {
        Vector2 tiling = Vector2.one * (1- healthManager.GetHealth()) * 10000;
        crackMat.mainTextureScale = tiling;

        float colorLerp = Mathf.Clamp(healthManager.GetHealth() * 1f, 0, 1);
        healthyMat.SetColor("_BaseColor", Color.Lerp(unHealthyColor, healthyColor, colorLerp));
    }
}
