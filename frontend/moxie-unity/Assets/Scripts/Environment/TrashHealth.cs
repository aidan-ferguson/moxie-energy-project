using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TrashHealth : UpdatableHealthObject
{
    [SerializeField]
    List<TrashModel> allChildren;

    [SerializeField]
    int iterateCount;

    [SerializeField]
    float popEffect;
    private void Awake()
    {
        foreach(Transform t in transform)
        {

            t.gameObject.AddComponent<TrashModel>();
            allChildren.Add(t.GetComponent<TrashModel>());
        }
    }
    public override void UpdateObject()
    {
        iterateCount = Mathf.RoundToInt(allChildren.Count * Mathf.Clamp(healthManager.GetHealth(), 0, 1));
        foreach(TrashModel child in allChildren)
        {
            child.isVisible = true;
        }
        for(int i = 0; i < iterateCount; i++)
        {
            allChildren[i].isVisible = false;
        }
        foreach(TrashModel child in allChildren)
        {
            child.UpdateTrash();
        }
    }

    
}
