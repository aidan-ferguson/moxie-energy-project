using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using UnityEngine;

[ExecuteInEditMode]
public class PropSpawner : MonoBehaviour
{
    public float radius = 1;
    public Vector2 regionSize = Vector2.one;
    public int rejectionSamples = 30;

    List<Vector2> points2D;

    [SerializeField]
    List<Vector3> points3D;

    public List<GameObject> prefabsToSpawn;

    public bool useSurfaceNormal;

    public Vector2 sizeVariance;

    public float verticalOffset;

    public bool updateObjects;

    private void Update()
    {
        if(updateObjects)
        {
            updateObjects = false;
        }
    }

    void CreateProceduralObjects()
    {
        points3D.Clear();
        points2D = PoissonDiskSampling.GeneratePoints(radius, regionSize, rejectionSamples);
        foreach (Vector2 point in points2D)
        {
            points3D.Add(new Vector3(point.x, 0f, point.y));
        }

        foreach (Transform objectToDestroy in transform)
        {
            StartCoroutine(DestroyRoutine(objectToDestroy.gameObject));
        }

        foreach (Vector3 point in points3D)
        {
            RaycastHit hit;
            if (Physics.Raycast(point + transform.position, -Vector3.up, out hit, Mathf.Infinity))
            {
                GameObject newPrefab = Instantiate(prefabsToSpawn[Random.Range(0, prefabsToSpawn.Count)], hit.point, Quaternion.identity, transform);
                if (useSurfaceNormal)
                {
                    newPrefab.transform.rotation = Quaternion.FromToRotation(newPrefab.transform.up, hit.normal);
                }
                float randomSize = Random.Range(sizeVariance.x, sizeVariance.y);
                newPrefab.transform.Rotate(newPrefab.transform.up, Random.Range(1, 360));
                newPrefab.transform.localScale = Vector3.one * randomSize;
                newPrefab.transform.position += Vector3.up * verticalOffset * randomSize;
            }
        }
    }

    void OnValidate()
    {
        if (gameObject.activeInHierarchy)
        {
            CreateProceduralObjects();
        }
    }

    IEnumerator DestroyRoutine(GameObject go)
    {
        yield return null;
        DestroyImmediate(go);
    }

    void OnDrawGizmosSelected()
    {
/*        Gizmos.DrawWireCube(new Vector3(regionSize.x, 0f, regionSize.y) / 2, new Vector3(regionSize.x, 0f, regionSize.y));
        if (points3D != null)
        {
            foreach (Vector3 point in points3D)
            {
                Gizmos.color = Color.green;
                Gizmos.DrawSphere(point, displayRadius);
            }
        }*/
    }
}
