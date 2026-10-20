package com.github.syren_dev_tech.hydra.cache.object;

import org.joml.Vector3f;

/**
 * Vertex data holder
 *
 * @param position The position of the vertex
 * @param texU The texture U coordinate
 * @param texV The texture V coordinate
 */
public record HydraVertex(Vector3f position, float texU, float texV) {
	public HydraVertex(double x, double y, double z) {
		this(new Vector3f((float)x, (float)y, (float)z), 0, 0);
	}

	public HydraVertex withUVs(float texU, float texV) {
		return new HydraVertex(this.position, texU, texV);
	}
}