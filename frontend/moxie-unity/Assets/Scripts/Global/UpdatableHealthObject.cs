using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public abstract class UpdatableHealthObject : MonoBehaviour
{
    [SerializeField]
    protected healthManager healthManager;

    [SerializeField]
    protected Color healthyColor;
    [SerializeField]
    protected Color unHealthyColor;

    public abstract void UpdateObject();
}
