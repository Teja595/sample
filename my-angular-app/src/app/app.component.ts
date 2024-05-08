import { Component, OnInit ,ElementRef, ViewChild } from '@angular/core';
import { User } from './user';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// import * as moment from 'moment';
import 'chartjs-adapter-date-fns';
import { ChartConfiguration, registerables } from 'chart.js';
import  'chartjs-plugin-zoom';
import Map from 'ol/Map';
import View from 'ol/View';
import { fromLonLat } from 'ol/proj';
import { FeatureLike } from 'ol/Feature';
import { StyleFunction } from 'ol/style/Style';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import VectorSource from 'ol/source/Vector';
import { Icon  } from 'ol/style';
import VectorLayer from 'ol/layer/Vector';
import { LineString, Point,Geometry } from 'ol/geom';
import { Feature } from 'ol';
import {Circle as CircleStyle, Fill, Stroke, Style } from 'ol/style';
import { Coordinate } from 'ol/coordinate';
Chart.register(...registerables);
import { toLonLat } from 'ol/proj';

import { useGeographic } from 'ol/proj';
import { Attribution, defaults as defaultControls } from 'ol/control';

// Chart.register(zoomPlugin);
// import { Chart, ChartConfiguration, registerables } from 'chart.js';
import 'chartjs-plugin-zoom';

Chart.register(...registerables);
import Chart from 'chart.js/auto';


interface DeviceIdCount {
  deviceId: string;
  count: number;
  speed:string;
  time: string;
  distance:string;
  avg_speed:string;
  start_date:string;
  end_date:string;
  timeDifference :string;

}

// Define a dataset interface to type the datasets
interface DataSet {
  label: string;
  data: number[];
  borderColor: string;
  pointBackgroundColor: string;
  fill: boolean;
  borderWidth: number;
  pointRadius: number;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit{
  title = 'Device ID data';
  map!: Map;
  originalUsers: User[] = []; // Stores the fetched data
  users: User[] = []; // Stores the data to display, after applying filters
  currentPage: number = 0;
  rowsPerPage: number = 10000;
  totalDistance: number = 0; // Initialize totalDistance
  chart: any;
  totalPages: number = 0;
  message: string = '';
  deviceId: string = ''; 
  filterDate: string = ''; // Holds the date part
  filterTime: string = ''; // Holds the time part (optional)
  startDate: string = '';
  endDate: string = '';
  selectedData: any = null; 
  totalRows: number = 0; // New property to store the total count of rows
  deviceIdCounts: DeviceIdCount[] = []; // New property for unique device IDs and counts
  detailsRequested: boolean = false; 
  private routeLayer: VectorLayer<VectorSource<Feature<Geometry>>> | null = null;

  private apiUrl = 'http://learn2code.redgrape.tech:8081/x/unique-device-ids';
  // @ViewChild('canvas', { static: true }) canvas!: ElementRef<HTMLCanvasElement>;
  constructor(private http: HttpClient) {useGeographic(); }
 
  ngOnInit() {
    this.fetchDeviceIdCounts();
    
    this.map = new Map({
      target: 'map',
      layers: [
        new TileLayer({
          source: new OSM()
        })
      ],
      view: new View({
        center: [0, 0],
        zoom: 2
      }),
       // Use 'new' to instantiate controls
       controls: defaultControls().extend([
        new Attribution({
            collapsible: false
        })
      ])
    });

    // this.loadPath(url);
    // this.setupClickHandler(this.map);
      // Optionally remove attribution control
      this.removeAttribution();
    
  }
  private removeAttribution() {
    const attributionControl = this.map.getControls().getArray().find(control => control instanceof Attribution);
    if (attributionControl) {
        this.map.removeControl(attributionControl);
    }
}
  private renderChart(users: User[]): void {
    const canvas = document.getElementById('canvas') as HTMLCanvasElement;
    if (!canvas) {
      console.error("Canvas element not found.");
      return;
  }
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      console.error("2D context not supported.");
      return;
    }

    const timeLabels = users.map(user => user.humanReadableDate);
    
    const datasets = this.categorizeData(users);
    
    
    this.chart?.destroy();

    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: timeLabels,
        datasets: datasets
      },
      options: this.getChartOptions()
    });

    // this.setupZoomButtons();
  }

  private categorizeData(users: User[]): DataSet[] {
    const stationaryData: DataSet = { label: 'Stationary (Speed < 3 km/hr)', data: [], borderColor: 'red', pointBackgroundColor: 'red', fill: false, borderWidth: 1, pointRadius: 2 };
    const walkingData: DataSet = { label: 'Walking (3 km/hr ≤ Speed ≤ 10 km/hr)', data: [], borderColor: 'green', pointBackgroundColor: 'green', fill: false, borderWidth: 1, pointRadius: 2 };
    const bikingData: DataSet = { label: 'Biking (Speed > 10 km/hr)', data: [], borderColor: 'blue', pointBackgroundColor: 'blue', fill: false, borderWidth: 1, pointRadius: 2 };

    users.forEach(user => {
        if (user.mov_avg_spd < 3) {
            stationaryData.data.push(user.mov_avg_spd);
        } else if (user.mov_avg_spd <=10) {
            walkingData.data.push(user.mov_avg_spd);
        } else {
            bikingData.data.push(user.mov_avg_spd);
        }
    });

    return [stationaryData, walkingData, bikingData];
}
private clearExistingRoute() {
  if (this.routeLayer) {
    this.map.removeLayer(this.routeLayer);
    this.routeLayer = null;
  }
}
// Add a new route layer
private addRouteLayer(vectorSource: VectorSource<Feature<Geometry>>): void {
  this.routeLayer = new VectorLayer({
    source: vectorSource,
  });
  this.map.addLayer(this.routeLayer);
}
loadPath(url: string): void {
  this.clearExistingRoute();
  this.http.get<{ content: any[] }>(url).subscribe({
    next: (response) => {
      if (response.content && Array.isArray(response.content)) {
        const validPoints = response.content.filter(point => point.longitude && point.latitude);

        const features = validPoints.map(point => {
          // Create a separate feature for each point
          const feature = new Feature({
            geometry: new Point([point.longitude, point.latitude]),
            data: { // Ensure only relevant data is attached here
              id: point.id,
              deviceId: point.deviceId,
              latitude: point.latitude,
              longitude: point.longitude,
              
              
              epochData: point.epochData,
              epochStored: point.epochStored,
              
              
              delta_distance: point.delta_distance,
              delta_t : point.delta_t,
              speed: point.speed,
              mov_avg_spd: point.mov_avg_spd,
              humanReadableDate : point.humanReadableDate,
              db_t:point.db_t,
              isActive: point.isActive
            }
          });
          // feature.setId(point.id); // Optionally set an ID for each feature for easy retrieval
          feature.setStyle(this.createFeatureStyle(feature));
          return feature;
        });

        // Create a LineString feature for the path if needed
        const coordinates = validPoints.map(point => [point.longitude, point.latitude]);
        const pathFeature = new Feature(new LineString(coordinates));
        // pathFeature.set('type', 'path'); // Tagging for styling or interactions
        pathFeature.setProperties(features);
        const p = new Feature(new VectorLayer);
        // Add start and end point features with special styles, if needed
        const startPointFeature = features[0];
        startPointFeature.setStyle(new Style({
          image: new CircleStyle({
            radius: 7,
            fill: new Fill({ color: 'green' }),
            stroke: new Stroke({ color: 'black', width: 2 })
          })
        }));

        const endPointFeature = features[features.length - 1];
        endPointFeature.setStyle(new Style({
          image: new CircleStyle({
            radius: 7,
            fill: new Fill({ color: 'red' }),
            stroke: new Stroke({ color: 'black', width: 2 })
          })
        }));

        // Create a vector source and add these features to it
        const vectorSource = new VectorSource({
          features: [pathFeature, ...features,p] // Include the path feature if needed
        });

        // Create and add the route layer
        this.routeLayer = new VectorLayer({
          source: vectorSource,
          // style: this.createPathStyle() // Implement this method to handle styles
        });
        this.map.addLayer(this.routeLayer);

        // Adjust map view to fit all coordinates
        if (coordinates.length > 0) {
          this.map.getView().fit(vectorSource.getExtent(), {
            padding: [50, 50, 50, 50],
            maxZoom: 17
          });
        }
// Fetch additional data from GeoNames API for each point
validPoints.forEach((point, index) => {
  const geoNamesUrl = `http://api.geonames.org/findNearbyPlaceNameJSON?lat=${point.latitude}&lng=${point.longitude}&username=teja`;
  this.http.get(geoNamesUrl).subscribe((geoNamesResponse: any) => {
    // Handle GeoNames API response here
    console.log('GeoNames API response:', geoNamesResponse);
    // Add GeoNames data to feature
    if (features[index]) {
      features[index].getProperties()['data'].geoNames = geoNamesResponse;
    }
  }, error => {
    console.error('Error fetching data from GeoNames API:', error);
  });
});

} else {
console.error('Data is not in expected format:', response);
}
},
error: (err) => {
console.error('Error loading path data:', err);
}
});
}
// Adjust the function to immediately return a Style object
private createFeatureStyle(feature: Feature): Style {
  // Assume there might be a rotation value in degrees in the feature's data
  const rotationDegrees = feature.get('rotation') || 0;
  const rotationRadians = rotationDegrees * (Math.PI / 180);  // Convert degrees to radians

  return new Style({
    image: new Icon({
      src: 'assets/arrow.png',    // Path to the icon
      anchor: [0.5, 0.5],         // Anchor the icon in the middle
      rotateWithView: true,
      rotation: rotationRadians,  // Apply rotation
      scale: 0.5                  // Adjust scale based on your needs
    }),
    stroke: new Stroke({         // Optional: if you also want to add outline properties
      color: '#0000FF',
      width: 2
    })
  });
}
ngAfterViewInit(): void {
  setTimeout(() => this.map.updateSize(), 0); // Ensures the map is correctly sized when your view initializes.

  this.map.on('singleclick', (event) => {
    this.map.forEachFeatureAtPixel(event.pixel, (feature) => {
      const geometry = feature.getGeometry();
      if (geometry instanceof Point) {
        const coordinates = geometry.getCoordinates(); // Returns coordinates in map projection
        const lonLat = toLonLat(coordinates, this.map.getView().getProjection()); // Convert to longitude and latitude

        // Optional: format coordinates to a fixed number of decimal places for clarity
        const formattedLon = lonLat[0].toFixed(7); // Longitude with 7 decimal places
        const formattedLat = lonLat[1].toFixed(7); // Latitude with 7 decimal places

        // console.log('Latitude: ', formattedLat, '\nLongitude: ', formattedLon);
        this.selectedData = feature.get('data');
        return true; // Stop iterating through other features
      }
      return false; // Continue to next feature if this is not a Point
    });
  });

  setTimeout(() => this.map.updateSize(), 0); // Ensure the map size is updated again if necessary
}


private handleFeatureClick(feature: Feature<any>): void {
  const properties = feature.getProperties(); // Get all properties
  // console.log("Properties before removal:", properties); // Check what you have before removal
console.log(feature);
  // Retrieve the data property
  // const data = properties['data'];
  // if (data) {
  //   console.log("Data attached to feature:", data);
  //   // You can now display this data in a popup or any other UI element
  //   // this.showPopup(data, feature.getGeometry().getCoordinates());
  // } else {
  //   console.error("No additional data found on feature.");
  // }
}





private showPopup(content: string): void {
  const popupElement = document.getElementById('map-popup');
  if (popupElement) {
    popupElement.innerHTML = content;
    popupElement.style.display = 'block';
  }
}

// private createPathStyle(): StyleFunction {
//   return (feature: FeatureLike, resolution: number): Style[] => {
//     // Get the geometry as a LineString; be careful with direct casting
//     const geometry = feature.getGeometry();
//     const data = feature.get('data');
//     // if (!(geometry instanceof LineString)) return []; // Ensure it's a LineString

//     const styles: Style[] = [
//       new Style({
//         stroke: new Stroke({
//           color: '#0000FF',
//           width: 2
//         })
//       })
//     ];

//     // Adding direction arrows; only if geometry is LineString and not RenderFeature
//     if (geometry instanceof LineString) {
//       geometry.forEachSegment((start, end) => {
//         const dx = end[0] - start[0];
//         const dy = end[1] - start[1];
//         const rotation = Math.atan2(dy, dx);
//         styles.push(new Style({
//           geometry: new Point(end),
//           image: new Icon({
//             src: 'assets/arrow.png',
//             anchor: [0.5, 0.5],
//             rotateWithView: true,
//             rotation: -rotation,
//             scale: 0.5
//           }),
//         // zIndex : 10
//         }));
//       });
//     }

//     return styles;
//   };
// }



// private setupClickHandler(map: Map): void {
  
//   map.on('click', (evt) => {
    
//     map.forEachFeatureAtPixel(evt.pixel, (feature) => {
//       if (feature instanceof Feature) { // Ensure it's a full feature
//         let styles: Style | Style[] | StyleFunction | undefined = feature.getStyle();

//         // If styles is a StyleFunction, evaluate it to obtain Style or Style[]
//         // if (typeof styles === 'function') {
//         //   // Get the resolution, ensuring it is not undefined
//         //   const resolution = map.getView().getResolution() ?? 1; // Default to 1 if undefined
//         //   styles = styles(feature, resolution);
//         // }

//         // Check if styles is an array or a single style object
//         if (Array.isArray(styles)) {
//           // Process each style to find the arrow icon
//           if (styles.some(style => this.isArrowIcon(style))) {
//             const user = feature.getProperties();
            
//             // this.displayData(user, evt.coordinate); // Pass coordinate for positioning popup
//             return true; // Stop processing other features
//           }
//         } 
//         // else if (styles && this.isArrowIcon(styles)) {
//         //   // Check a single style object
//         //   const user = feature.getProperties();
//         //   // this.displayData(user, evt.coordinate);
//         //   return true;
//         // }
//       }
//       return false; // Continue to next feature if no match found
//     });
//   });
// }

private isArrowIcon(style: Style): boolean {
  const image = style.getImage();
  return image instanceof Icon && image.getSrc() === 'assets/arrow.png';
}
close():void{
  this.selectedData = null;
}

private getChartOptions() {
  return {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'top' as const,
      },
      zoom: {
        pan: {
          enabled: true,
          mode: 'x' as const,
        },
        zoom: {
          wheel: { enabled: true },
          pinch: { enabled: true },
          mode: 'x' as const,
        },
      },
    },
    scales: {
      x: {
        type: 'time' as const,
        time: { unit: 'minute' as const },
        title: { display: true, text: 'Time', color: '#333' },
        ticks: { color: '#666' },
      },
      y: {
        title: { display: true, text: 'Moving avg Speed (km/hr)', color: '#333' },
        ticks: { color: '#666' },
      }
    }
  };
}

  zoomChart(direction: string) {
    const factor = direction === 'in' ? 0.9 : 1.1; // Smaller factor zooms in, larger zooms out
  
    if (this.chart) {
        const xScale = this.chart.scales.x;
  
        // Calculate new min and max
        let newMin = xScale.min && new Date(new Date(xScale.min).getTime() * factor);
        let newMax = xScale.max && new Date(new Date(xScale.max).getTime() * factor);
  
        // Adjust the scale options directly
        this.chart.options.scales.x.min = newMin ? newMin.toISOString() : xScale.min;
        this.chart.options.scales.x.max = newMax ? newMax.toISOString() : xScale.max;
  
        // Update chart to reflect new scale values
        this.chart.update();
        
    }
}

  
  

fetchData(page: number, rows: number, deviceId?: string, startDate?: string, endDate?: string): void {
  let url:string = `http://learn2code.redgrape.tech:8081/x?page=${page}&size=${rows}`;
  if (deviceId) {
    url += `&deviceId=${deviceId}`;
  }
  if (startDate) {
    url += `&startTime=${encodeURIComponent(startDate)}`;
  }
  if (endDate) {
    url += `&endTime=${encodeURIComponent(endDate)}`;
  }
  console.log(startDate);
this.loadPath(url);
  this.http.get<any>(url).subscribe(
    response => {
  
      if (response && response.content && typeof response.totalPages === 'number') {
        this.totalDistance = 0;

     // Apply type to the filter function's parameter
     const filteredUsers = response.content;

        this.originalUsers = filteredUsers.map((user: any) => {
          this.totalDistance += parseFloat(user.delta_distance) || 0;
          return {
            ...user,
            isActive: true
          };
        });

        this.users = [...this.originalUsers];
        this.renderChart(this.originalUsers);
        this.totalPages = response.totalPages;
        this.totalRows = response.totalElements;
        this.message = '';
      } else {
        console.error('Unexpected response structure:', response);
        this.message = 'Failed to load data due to unexpected response structure.';
      }
    },
    error => {
      if (error.status === 404) {
        this.message = 'No data present for the given device ID.';
        this.users = []; // Clear any existing data
      } else {
        console.error('There was an error!', error);
        this.message = 'An error occurred while fetching data.';
      }
    }
  );
}

  fetchTotalDistance(deviceId: string, startDate: string, endDate: string): void {
    const url = `http://learn2code.redgrape.tech:8081/x/totalDistance?deviceId=${deviceId}&startTime=${encodeURIComponent(startDate)}&endTime=${encodeURIComponent(endDate)}`;
    
    this.http.get<{totalDistance: number}>(url).subscribe(
      response => {
        this.totalDistance = response.totalDistance;
      },
      error => {
        console.error('There was an error fetching the total distance:', error);
      }
    );
  }
  
  goToDeviceDetails(deviceId: string,start_date:string,end_date:string): void {
    
    
    this.deviceId = deviceId;
    // Assuming deviceIdCounts is an array of objects and you're looking for an object where a certain property (e.g., start_date) matches this.startDate
    // this.startDate = start_date ;
    // this.endDate = end_date ;
    this.startDate = this.formatDateTime(start_date);
    this.endDate = this.formatDateTime(end_date);
    
    // Optionally reset dates or set them to a default range
    // this.startDate = '2023-01-01T00:00';
    // this.endDate = '2023-12-31T23:59';
    this.detailsRequested = true; 
    // this.fetchDataWithFilters();
  }
  
  fetchDataWithDeviceId(): void {
    this.fetchData(this.currentPage, this.rowsPerPage, this.deviceId);
  }
  fetchDataWithFilters(): void {
    // Call fetchData to get the paginated data
    
    
    this.fetchData(this.currentPage, this.rowsPerPage, this.deviceId, this.startDate, this.endDate);
    
    // Additionally, fetch the total distance for all data matching the filters
    this.fetchTotalDistance(this.deviceId, this.startDate, this.endDate);
    
  }
   formatDateTime(dateTimeString: string): string {
    // First, replace spaces with 'T' to conform to the ISO 8601 format which the Date constructor can parse
    const compliantDateTimeString = dateTimeString.replace(' ', 'T');

    const date = new Date(compliantDateTimeString);
    if (isNaN(date.getTime())) {
        throw new Error('Invalid date-time string');
    }

    const year = date.getFullYear();
    const month = date.getMonth() + 1; // getMonth() is zero-indexed
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();

    // Pad single digits with leading zeros
    const formattedMonth = month < 10 ? `0${month}` : month.toString();
    const formattedDay = day < 10 ? `0${day}` : day.toString();
    const formattedHours = hours < 10 ? `0${hours}` : hours.toString();
    const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes.toString();

    return `${year}-${formattedMonth}-${formattedDay}T${formattedHours}:${formattedMinutes}`;

}



  fetchDeviceIdCounts(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      response => {
        const deviceIds = response.map(item => item[0]);

        // If you want to store these device IDs in your component or service
        this.handleDeviceIds(deviceIds);
        this.deviceIdCounts = response.map(item => ({
          deviceId: item[0],
          count: item[1],
          speed:item[2],
          time: item[3],
          distance:item[4],
          avg_speed:item[5],
          start_date:item[6],
          end_date:item[7],
          timeDifference:item[8]
        
        }));
        
      },
      
      error => {
        console.error('There was an error fetching the device ID counts:', error);
      }
    );
  }
  // Example handler function, implement based on your needs
handleDeviceIds(deviceIds: string[]): void {
  // Processing logic here, such as setting state or emitting an event
  
}

  onNextClick(): void {
    if (this.currentPage < this.totalPages - 1) { // Adjusted to account for zero-based index
      this.currentPage++;
      this.message = ''; // Reset message
      this.fetchData(this.currentPage, this.rowsPerPage, this.deviceId, this.startDate, this.endDate);
    } else {
      // Set a message when there are no more pages
      this.message = 'No more rows.';
    }
  }
  
  onPrevClick(): void {
    if (this.currentPage > 0) { // Ensure we don't go below the first page
      this.currentPage--;
      this.message = ''; // Reset message
      this.fetchData(this.currentPage, this.rowsPerPage, this.deviceId, this.startDate, this.endDate);
    } else {
      // Optionally, set a message or handle as necessary
      this.message = 'You are on the first page.';
    }
  }
// downloadRepo(deviceId:string,startDate:string,endDate:string):void{
// const x =  this.fetchData(this.currentPage, this.rowsPerPage, deviceId, startDate, endDate);
//  let csvData = this.prepareCsvDataForDownload(x);
// }
downloadReport(deviceId: string, start_date: string, end_date: string): void {
  // Convert start and end dates to the required format
  const formattedStartDate = this.formatDateTime(start_date);
  const formattedEndDate = this.formatDateTime(end_date);

  // Define a temporary method to fetch and process the data
  const url = `http://learn2code.redgrape.tech:8081/x?deviceId=${deviceId}&startTime=${encodeURIComponent(formattedStartDate)}&endTime=${encodeURIComponent(formattedEndDate)}&size=10000`; // Assuming there's a limit to how many records can be fetched at once
  this.http.get<any>(url).subscribe(
    response => {
      if (response && response.content  && response.content.length > 0) {
        // Ensure the first row remains unfiltered
        const firstRow = response.content[0];
        // Apply filtering to subsequent rows
        const remainingContent = response.content.slice(1);
        const filteredContent = remainingContent.filter((user: any) => user.speed > 10 && user.speed < 65);

        // Reassemble the data with the first row unfiltered
        const finalData = [firstRow, ...filteredContent];

        // Process and download the CSV with filtered data
        let csvData = this.prepareCsvDataForDownload(response.content);
        this.downloadCsv(csvData, `${deviceId}-report.csv`); // Use a dynamic name for the CSV file
      } else {
        console.error('Unexpected response structure:', response);
      }
    },
    error => {
      console.error('There was an error fetching the detailed data:', error);
    }
  );
}

prepareCsvDataForDownload(data: any[]): string {
  // Adjust this method to format the detailed data into CSV
  let csvContent = 'ID,Device ID,Epoch Data,Epoch Stored,Latitude,Longitude,Delta Distance(km),Delta T(sec),Speed(km/hr),Moving Average Speed,Date and Time\n';

  data.forEach((detail) => {
    let row = `${detail.id},${detail.deviceId},${detail.epochData},${detail.epochStored},${detail.latitude},${detail.longitude},${detail.delta_distance},${detail.delta_t},${detail.speed},${detail.mov_avg_spd},${detail.humanReadableDate}`;
    csvContent += row + '\n';
  });

  return csvContent;
}

downloadCsv(csvData: string, filename: string): void {
  let blob = new Blob([csvData], { type: 'text/csv;charset=utf-8;' });
  let url = window.URL.createObjectURL(blob);

  let a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
}

 createArrowFeatures(lineFeature: Feature<LineString>, data: any): Feature<Point>[] {
  // Check if geometry is defined and is a LineString before proceeding
  const geometry = lineFeature.getGeometry();
  const arrowFeatures: Feature<Point>[] = [];

  if (geometry instanceof LineString) {
    // Now safe to call forEachSegment because we've checked geometry is not undefined
    geometry.forEachSegment((start, end) => {
      const dx = end[0] - start[0];
      const dy = end[1] - start[1];
      const rotation = Math.atan2(dy, dx);
      const midpoint = [(start[0] + end[0]) / 2, (start[1] + end[1]) / 2];

      // Create a feature for each segment's midpoint
      const arrowFeature = new Feature(new Point(midpoint));
      arrowFeature.setProperties({
        ...data,
        rotation: rotation
      });
      
      arrowFeatures.push(arrowFeature);
    });
  } else {
    // Log an error or handle cases where geometry is not as expected
    console.error('Expected LineString geometry but got something else or undefined.');
  }

  return arrowFeatures;
}// Define a style function for arrows
// Ensure this is declared before use
 arrowStyleFunction: (feature: FeatureLike, resolution?: number) => Style | Style[] | undefined = (feature: FeatureLike, resolution?: number) => {
  if (feature instanceof Feature && feature.getGeometry() instanceof Point) {
    const rotation: number = feature.get('rotation'); // Ensure rotation is stored correctly
    return new Style({
      image: new Icon({
        src: 'assets/arrow.png',
        anchor: [0.5, 0.5],
        rotateWithView: true,
        rotation: -rotation,
        scale: 0.5
      })
    });
  }
  // Return undefined if the feature does not match the expected conditions
  return undefined;
};
// Example usage within your application logic
 someLineFeature = new Feature({
  geometry: new LineString([[0, 0], [100, 100]]) // Example coordinates
});

 someData = { isActive: true }; // Example data

 arrowFeatures = this.createArrowFeatures(this.someLineFeature, this.someData);
 arrowSource = new VectorSource({
  features: this.arrowFeatures
});

 arrowLayer = new VectorLayer({
  source: this.arrowSource,
  style:this.arrowStyleFunction  // Define a style function to style these arrows
});

olMap = new Map({
  layers: [this.arrowLayer],
  target: 'mmap',  // The target DOM element id should be unique
  view: new View({
    center: [0, 0],
    zoom: 4
  })
});


}

