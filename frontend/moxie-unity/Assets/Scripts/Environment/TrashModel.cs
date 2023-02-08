using System.Collections;
using System.Collections.Generic;
using System.Threading;
using UnityEngine;

public class TrashModel : MonoBehaviour
{
    public bool isVisible;

    [SerializeField]
    bool isVisibleBefore;
    private void Start()
    {
        scale = transform.localScale;
    }
    public Vector3 scale;

    float f = 10;

    public void PopIn()
    {
        StopAllCoroutines();
        StartCoroutine(PopInRountine());
    }


    public void UpdateTrash()
    {
        if(isVisible != isVisibleBefore)
        {
            isVisibleBefore = isVisible;
            if(!isVisible)
            {
                PopOut();
            }
            else
            {
                PopIn();
            }
        }
    }
    public void PopOut()
    {
        StopAllCoroutines();
        StartCoroutine(PopOutRoutine());
    }
    IEnumerator PopInRountine()
    {
        Vector3 scale = transform.GetComponent<TrashModel>().scale;
        while (transform.localScale != scale)
        {
            transform.localScale = Vector3.Slerp(transform.localScale, scale, f * Time.deltaTime);
            yield return null;
        }
        yield break;
    }

    IEnumerator PopOutRoutine()
    {
        while (transform.localScale != Vector3.zero)
        {
            transform.localScale = Vector3.Slerp(transform.localScale, Vector3.zero, f * Time.deltaTime);
            yield return null;
        }
        yield break;
    }
}
