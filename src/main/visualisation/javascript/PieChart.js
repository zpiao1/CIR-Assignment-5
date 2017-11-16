
var data = [
{
  values: [16, 15, 12, 40],
  labels: ['Conference A', 'Conference B', 'Conference C', 'Others' ],
  domain: {
    //x: [0, .48]
    x: [0, 1]
  },
  name: 'Number of Docs Published',
  hoverinfo: 'label+percent+name',
  hole: .4,
  type: 'pie'
},

/*
{
  values: [27, 11, 25, 8, 1, 3, 25],
  labels: ['US', 'China', 'European Union', 'Russian Federation', 'Brazil', 'India', 'Rest of World' ],
  text: 'CO2',
  textposition: 'inside',
  domain: {
    x: [.52, 1]
  },
  name: 'CO2 Emissions',
  hoverinfo: 'label+percent+name',
  hole: .4,
  type: 'pie'
}
*/
];

var layout = {
  title: 'Trend Three: Top 3 Conferences with the Most Number of Papers Published',
  annotations: [
    {
      font: {
        size: 14
      },
      showarrow: false,
      text: 'CIR',
      //x: 0.17,
      x: 0.50,
      y: 0.5
    },
    /*
    {
      font: {
        size: 14
      },
      showarrow: false,
      text: 'CO2',
      x: 0.82,
      y: 0.5
    }
    */
  ]
};

Plotly.newPlot('pieChart', data, layout);

