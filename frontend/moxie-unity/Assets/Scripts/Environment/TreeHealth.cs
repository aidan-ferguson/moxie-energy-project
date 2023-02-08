using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TreeHealth : UpdatableHealthObject
{
    [SerializeField]
    float heightScaleFactor;

    [SerializeField]
    List<Material> materials;
    public override void UpdateObject()
    {
        foreach(Transform tree in transform)
        {
            tree.localScale = new Vector3(healthManager.GetHealth(),
                Mathf.Clamp(healthManager.GetHealth() * heightScaleFactor, 0, 1),
                healthManager.GetHealth());
        }

        foreach(Material mat in materials)
        {
            mat.SetFloat("_Metallic", 1- healthManager.GetHealth());
        }
    }
}
