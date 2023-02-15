using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GrassHealth : UpdatableHealthObject
{
    [SerializeField]
    float heightScaleFactor;

    [SerializeField]
    Vector2 minSize;

    [SerializeField]
    Material mat;

    public override void UpdateObject()
    {
        foreach (Transform grass in transform)
        {
            float minX = Mathf.Clamp(healthManager.GetHealth(), minSize.x, 1);
            float minY = Mathf.Clamp(healthManager.GetHealth() * heightScaleFactor, minSize.y, 1);
            grass.localScale = new Vector3(minX,
                minY,
                minX);
        }

        mat.SetFloat("_Metallic", 1 - healthManager.GetHealth());
    }
}
