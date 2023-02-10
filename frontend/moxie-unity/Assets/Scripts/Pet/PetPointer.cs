using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PetPointer : MonoBehaviour
{
    Vector3 targetPos;
    void Update()
    {
        Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
        RaycastHit hit;
        if (Physics.Raycast(ray, out hit, Mathf.Infinity))
        {
            targetPos = hit.point;
        }
    }

    public Vector3 GetPos()
    {
        return targetPos;
    }    
}
