using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TreeHealth : Updatable
{
    [SerializeField]
    float heightScaleFactor;

    [SerializeField]
    List<Material> materials;
    public override void UpdateObject()
    {
        foreach(Transform tree in transform)
        {
            tree.localScale = new Vector3(HealthManager.Instance().GetHealth(),
                Mathf.Clamp(HealthManager.Instance().GetHealth() * heightScaleFactor, 0, 1),
                HealthManager.Instance().GetHealth());
        }

        foreach(Material mat in materials)
        {
            mat.SetFloat("_Metallic", 1- HealthManager.Instance().GetHealth());
        }
    }
}
