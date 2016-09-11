package tonegod.emitter.shapes;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author t0neg0d
 * @edit JavaSaBr
 */
public class TriangleEmitterShape extends Mesh {

    protected final FloatBuffer vertexes;
    protected final ShortBuffer indexes;
    protected final FloatBuffer normals;

    protected final Vector3f firstPoint;
    protected final Vector3f secondPoint;
    protected final Vector3f thirdPoint;

    protected final Triangle triangle;

    protected float size;

    public TriangleEmitterShape() {
        this.vertexes = BufferUtils.createFloatBuffer(9);
        this.indexes = BufferUtils.createShortBuffer(3);
        this.normals = BufferUtils.createFloatBuffer(9);
        this.firstPoint = new Vector3f();
        this.secondPoint = new Vector3f();
        this.thirdPoint = new Vector3f();
        this.triangle = new Triangle();
    }

    /**
     * Re-init this mesh using the size.
     *
     * @param size the new size of this mesh.
     */
    public void init(final float size) {
        this.size = size;

        firstPoint.set(-(size / 2), 0, (size / 2));
        secondPoint.set((size / 2), 0, -(size / 2));
        thirdPoint.set(-(size / 2), 0, -(size / 2));

        triangle.set(firstPoint, secondPoint, thirdPoint);
        triangle.calculateCenter();

        firstPoint.subtractLocal(triangle.getCenter());
        secondPoint.subtractLocal(triangle.getCenter());
        thirdPoint.subtractLocal(triangle.getCenter());

        vertexes.clear();
        vertexes.put(firstPoint.getX());
        vertexes.put(firstPoint.getY());
        vertexes.put(firstPoint.getZ());
        vertexes.put(secondPoint.getX());
        vertexes.put(secondPoint.getY());
        vertexes.put(secondPoint.getZ());
        vertexes.put(thirdPoint.getX());
        vertexes.put(thirdPoint.getY());
        vertexes.put(thirdPoint.getZ());
        vertexes.flip();

        normals.clear();
        normals.put(0);
        normals.put(1);
        normals.put(0);
        normals.put(0);
        normals.put(1);
        normals.put(0);
        normals.put(0);
        normals.put(1);
        normals.put(0);
        normals.flip();

        indexes.clear();
        indexes.put((short) 0);
        indexes.put((short) 1);
        indexes.put((short) 2);
        indexes.flip();

        clearBuffer(VertexBuffer.Type.Position);
        setBuffer(VertexBuffer.Type.Position, 3, vertexes);
        clearBuffer(VertexBuffer.Type.Index);
        setBuffer(VertexBuffer.Type.Index, 3, indexes);
        clearBuffer(VertexBuffer.Type.Normal);
        setBuffer(VertexBuffer.Type.Normal, 3, normals);

        createCollisionData();
        updateBound();
    }

    @Override
    public void read(final JmeImporter importer) throws IOException {
        super.read(importer);
        final InputCapsule capsule = importer.getCapsule(this);
        final float size = capsule.readFloat("size", 1F);
        init(size);
    }

    @Override
    public void write(final JmeExporter exporter) throws IOException {
        super.write(exporter);
        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(size, "size", 1F);
    }
}