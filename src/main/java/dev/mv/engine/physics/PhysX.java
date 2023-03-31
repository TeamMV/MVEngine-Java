package dev.mv.engine.physics;

import de.fabmax.physxjni.Loader;
import dev.mv.utils.Utils;
import dev.mv.utils.logger.Logger;
import physx.common.*;
import physx.cooking.*;
import physx.geometry.PxConvexMesh;
import physx.physics.PxPhysics;
import physx.vehicle2.PxVehicleAxesEnum;
import physx.vehicle2.PxVehicleFrame;
import physx.vehicle2.PxVehicleTopLevelFunctions;

import static physx.PxTopLevelFunctions.*;

public class PhysX {

    public static final PxDefaultAllocator defaultAllocatorCallback;
    public static final PxFoundation foundation;
    //public static final PxPvd visualDebugger;
    public static final PxPhysics physics;
    public static final PxCooking cooking;
    public static final PxVehicleFrame vehicleFrame;
    public static final PxConvexMesh unitCylinderSweepMesh;
    public static final PxDefaultCpuDispatcher defaultCpuDispatcher;

    static {
        Loader.load();
        defaultAllocatorCallback = new PxDefaultAllocator();
        foundation = CreateFoundation(getPHYSICS_VERSION(), defaultAllocatorCallback, getErrorCallback());

        //visualDebugger = CreatePvd(foundation);
        //PxPvdTransport pvdTransport = DefaultPvdSocketTransportCreate("localhost", 5425, 10);
        //visualDebugger.connect(pvdTransport, new PxPvdInstrumentationFlags((byte) PxPvdInstrumentationFlagEnum.eALL.value));

        physics = CreatePhysics(getPHYSICS_VERSION(), foundation, new PxTolerancesScale());

        PxCookingParams cookingParams = new PxCookingParams(new PxTolerancesScale());
        cookingParams.setSuppressTriangleMeshRemapTable(true);
        PxMidphaseDesc midphaseDesc = new PxMidphaseDesc();
        midphaseDesc.setToDefault(PxMeshMidPhaseEnum.eBVH34);
        PxBVH34MidphaseDesc bhv34 = midphaseDesc.getMBVH34Desc();
        bhv34.setNumPrimsPerLeaf(4);
        midphaseDesc.setMBVH34Desc(bhv34);
        cookingParams.setMidphaseDesc(midphaseDesc);
        cooking = CreateCooking(getPHYSICS_VERSION(), foundation, cookingParams);

        PxVehicleTopLevelFunctions.InitVehicleExtension(foundation);
        vehicleFrame = new PxVehicleFrame();
        vehicleFrame.setLatAxis(PxVehicleAxesEnum.ePosX);
        vehicleFrame.setVrtAxis(PxVehicleAxesEnum.ePosY);
        vehicleFrame.setLngAxis(PxVehicleAxesEnum.ePosZ);

        unitCylinderSweepMesh = PxVehicleTopLevelFunctions.VehicleUnitCylinderSweepMeshCreate(vehicleFrame, physics, cookingParams);

        int workers = Utils.clamp(Runtime.getRuntime().availableProcessors() - 2, 1, 16);
        defaultCpuDispatcher = DefaultCpuDispatcherCreate(workers);
    }

    static boolean init() {
        return !Utils.isAnyNull(foundation, physics, cooking, vehicleFrame, unitCylinderSweepMesh, defaultCpuDispatcher);
    }

    static void terminate() {
        physics.release();
        foundation.release();
    }

    public static PxErrorCallback getErrorCallback() {
        return new PxErrorCallbackImpl() {
            @Override
            public void reportError(PxErrorCodeEnum code, String message, String file, int line) {
                Logger.error("Physx: " + code.name() + "' in file '" + file + "' line " + line + ". " + message);
            }
        };
    }
}
