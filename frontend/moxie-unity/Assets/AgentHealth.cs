using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;

public class AgentHealth : MonoBehaviour
{
    float speed;
    float acc;

    [SerializeField]
    NavMeshAgent agent;

    void Awake()
    {
        speed = agent.speed;
        acc = agent.acceleration;
    }

    void Update()
    {
        agent.speed = speed * HealthManager.Instance().GetHealth();
        agent.acceleration = acc * HealthManager.Instance().GetHealth() * HealthManager.Instance().GetHealth() * HealthManager.Instance().GetHealth();
    }
}
