using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class healthManager : MonoBehaviour
{
    [SerializeField]
    Slider slider;
    bool continuousUpdate;
    [SerializeField]
    bool isUpdating;
    [SerializeField]
    float health;
    UpdatableHealthObject[] updatables;

    private void Awake()
    {
        updatables = (UpdatableHealthObject[])FindObjectsOfType(typeof(UpdatableHealthObject));
    }

    public float GetHealth()
    {
        return health;
    }

    public void SetHealth(string new_health) {
        Debug.Log(new_health);
        health = float.Parse(new_health);
    }

    private void Start()
    {
        slider.onValueChanged.AddListener(delegate { ValueChangeCheck(); });
    }

    // Invoked when the value of the slider changes.
    public void ValueChangeCheck()
    {
        foreach (UpdatableHealthObject updatableObject in updatables)
        {
            updatableObject.UpdateObject();
        }
    }

    private void Update()
    {
        ValueChangeCheck();
        if (Input.GetKey("escape"))
        {
            Application.Quit();
        }
    }
}
