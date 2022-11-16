using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;

public class PetMovement : MonoBehaviour
{
    [SerializeField]
    NavMeshAgent agent;

    [SerializeField]
    PetPointer pointer;

    private void Update()
    {
        if(pointer.GetPos() != null)
        {
            agent.SetDestination(pointer.GetPos());
        }
    }
}
