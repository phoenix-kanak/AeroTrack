package com.project.aerotrack.utils
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateReferenceSystem
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate

object ConversionUtil {

    val epsgMapping = mapOf(
        "0(Yard)" to "EPSG:24370",
        "I(Metre)" to "EPSG:24378",
        "IIa(Metre)" to "EPSG:24379",
        "IIb(Metre)" to "EPSG:24380",
        "IIIa(Metre)" to "EPSG:24381",
        "IVa(Metre)" to "EPSG:24383"
    )

    fun convertFromEPSG24378Locally(
        x: Double,    // The x-coordinate (easting)
        y: Double,// The y-coordinate (northing)
        zone:String
    ): Pair<Double, Double> {
        val crsFactory = CRSFactory()
        val transformFactory = CoordinateTransformFactory()

        // Create the source CRS (EPSG:24378)
        val sourceCRS: CoordinateReferenceSystem = crsFactory.createFromName(epsgMapping[zone])
        // Define the target CRS (WGS84: EPSG:4326)
        val targetCRS: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")

        // Set up the transformation
        val transform = transformFactory.createTransform(sourceCRS, targetCRS)
        val srcCoord = ProjCoordinate(x, y)
        val destCoord = ProjCoordinate()
        transform.transform(srcCoord, destCoord)

        // Note: destCoord.x holds the longitude and destCoord.y the latitude.
        return Pair(destCoord.y, destCoord.x)
    }
//3631836 502371 1

    //3356652 1315152 delhi ke paas
    //3149582 1630478 jalandhar

//    landing 3219560 1744500 dharmshala
//    takeoff 3318511 1619285 shimla
}