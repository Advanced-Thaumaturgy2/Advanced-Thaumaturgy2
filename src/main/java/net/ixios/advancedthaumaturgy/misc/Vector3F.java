package net.ixios.advancedthaumaturgy.misc;

public class Vector3F
{

	public float x, y, z;
	
	public Vector3F(float x, float y, float z)
	{
		this.x = x; this.y = y; this.z = z;
	}
	
	public float distanceTo(Vector3F target)
	{
		float dx = (float)Math.pow(target.x - x, 2);
		float dy = (float)Math.pow(target.y - y, 2);
		float dz = (float)Math.pow(target.z - z, 2);
		return (float)Math.sqrt(dx + dy + dz);
	}
	
	public float distanceTo(Vector3 target)
	{
		float dx = (float)Math.pow(target.x - x, 2);
		float dy = (float)Math.pow(target.y - y, 2);
		float dz = (float)Math.pow(target.z - z, 2);
		return (float)Math.sqrt(dx + dy + dz);
	}
	
}
