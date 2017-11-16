//Modify X/Y axis range here
tracePeriod = [1990.5, 2011.5];
docPublished = [0, 15];

//Pass in the trace data here
trace1 = {
  x: ['1991', '1993', '1994', '2004', '2005', '2006', '2007', '2008', '2010', '2011'],
  y: ['2', '2', '13', '1', '9', '12', '11', '8', '13', '10'],

  line: {
    color: 'rgb(106, 168, 79)', 
    width: 4
  }, 
  name: 'Conference A', 
  type: 'scatter', 
  uid: '86bee4'
};

trace2 = {
  x: ['1991', '1993', '1994', '2004', '2005', '2006', '2007', '2008', '2010', '2011'],
  y: ['5', '1', '4', '7', '3', '2', '5', '10', '7', '2'],

  line: {
    color: 'rgb(204, 65, 37)', 
    width: 4
  }, 
  name: 'Conference B', 
  type: 'scatter', 
  uid: 'c82748'
};

/*
trace3 = {
  x: ['1970', '1971', '1972', '1973', '1974', '1975', '1976', '1977', '1978', '1979', '1980', '1981', '1982', '1983', '1984', '1985', '1986', '1987', '1988', '1989', '1990', '1991', '1992', '1993', '1994', '1995', '1996', '1997', '1998', '1999', '2000', '2001', '2002', '2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012'], 
  y: ['18.40', '17.25', '15.60', '14.82', '14.78', '14.61', '14.61', '15.10', '15.02', '15.59', '15.88', '15.80', '15.88', '15.59', '15.59', '15.79', '15.59', '15.67', '16.04', '16.41', '16.69', '16.20', '15.79', '15.33', '14.96', '14.59', '14.39', '14.18', '14.30', '14.18', '14.38', '14.09', '14.01', '14.09', '13.97', '13.97', '14.25', '14.25', '13.96', '13.55', '12.98', '12.69', '12.56'], 
  line: {
    color: 'rgb(60, 120, 216)', 
    width: 4
  }, 
  name: 'Total', 
  type: 'scatter', 
  uid: 'c03658'
};
data = [trace1, trace2, trace3];
*/
data = [trace1, trace2]
layout = {
  /* comment out the annotations */
  annotations: [
    {
      x: 0.1, 
      y: 0.3, 
      align: 'center', 
      arrowcolor: 'rgba(68, 68, 68, 0)', 
      arrowhead: 1, 
      arrowsize: 1, 
      arrowwidth: 0, 
      ax: 326, 
      ay: -182.5, 
      bgcolor: 'rgba(0,0,0,0)', 
      bordercolor: '', 
      borderpad: 1, 
      borderwidth: 1, 
      font: {
        color: 'rgb(60, 120, 216)', 
        family: '', 
        size: 14
      }, 
      opacity: 1, 
      showarrow: true, 
      //text: '<b>Total</b>', 
      textangle: 0, 
      xanchor: 'auto', 
      xref: 'paper', 
      yanchor: 'auto', 
      yref: 'paper'
    }, 
    {
      x: 0.1, 
      y: 0.3, 
      align: 'center', 
      arrowcolor: 'rgba(68, 68, 68, 0)', 
      arrowhead: 1, 
      arrowsize: 1, 
      arrowwidth: 0, 
      ax: 94, 
      ay: -10.5, 
      bgcolor: 'rgba(0,0,0,0)', 
      bordercolor: '', 
      borderpad: 1, 
      borderwidth: 1, 
      font: {
        color: 'rgb(204, 65, 37)', 
        family: '', 
        size: 14
      }, 
      opacity: 1, 
      showarrow: true, 
      //text: '<b>34-39 years old</b>', 
      textangle: 0, 
      xanchor: 'auto', 
      xref: 'paper', 
      yanchor: 'auto', 
      yref: 'paper'
    }, 
    {
      x: 0.1, 
      y: 0.3, 
      align: 'center', 
      arrowcolor: 'rgba(68, 68, 68, 0)', 
      arrowhead: 1, 
      arrowsize: 1, 
      arrowwidth: 0, 
      ax: 359, 
      ay: 37.5, 
      bgcolor: 'rgba(0,0,0,0)', 
      bordercolor: '', 
      borderpad: 1, 
      borderwidth: 1, 
      font: {
        color: 'rgb(106, 168, 79)', 
        family: '', 
        size: 14
      }, 
      opacity: 1, 
      showarrow: true, 
      //text: '<b>40-44 years old</b>', 
      textangle: 0, 
      xanchor: 'auto', 
      xref: 'paper', 
      yanchor: 'auto', 
      yref: 'paper'
    }, 
    {
      x: 0.1, 
      y: 0.3, 
      align: 'center', 
      arrowcolor: 'rgba(68, 68, 68, 0)', 
      arrowhead: 1, 
      arrowsize: 1, 
      arrowwidth: 0, 
      ax: 25, 
      ay: 174.5, 
      bgcolor: 'rgba(0,0,0,0)', 
      bordercolor: '', 
      borderpad: 1, 
      borderwidth: 1, 
      font: {
        color: '', 
        family: '', 
        size: 0
      }, 
      opacity: 1, 
      showarrow: true, 
      //text: 'Data: Centers for Disease Control and Prevention<br>Source: <a href="http://fivethirtyeight.com/datalab/dear-mona-i-dont-want-children-am-i-normal/">FiveThirtyEight</a>', 
      textangle: 0, 
      xanchor: 'auto', 
      xref: 'paper', 
      yanchor: 'auto', 
      yref: 'paper'
    }
  ], 
  /**/
  autosize: false, 
  bargap: 0.2, 
  bargroupgap: 0, 
  barmode: 'group', 
  boxgap: 0.3, 
  boxgroupgap: 0.3, 
  boxmode: 'overlay', 
  dragmode: 'zoom', 
  font: {
    color: '#444', 
    family: '"Open sans", verdana, arial, sans-serif', 
    size: 12
  }, 
  height: 600, 
  hidesources: false, 
  hovermode: 'x', 
  legend: {
    x: 0.765625, 
    y: 0.942857142857, 
    bgcolor: 'rgba(255, 255, 255, 0)', 
    bordercolor: '#444', 
    borderwidth: 0, 
    font: {
      color: '', 
      family: '', 
      size: 0
    }, 
    traceorder: 'normal', 
    xanchor: 'left', 
    yanchor: 'top'
  }, 
  margin: {
    r: 80, 
    t: 100, 
    autoexpand: true, 
    b: 80, 
    l: 80, 
    pad: 0
  }, 
  paper_bgcolor: 'rgb(240, 240, 240)', 
  plot_bgcolor: 'rgb(240, 240, 240)', 
  separators: '.,', 
  showlegend: false, 
  smith: false, 
  title: 'Trend Two: Comparison between Two Events', 
  titlefont: {
    color: '', 
    family: '', 
    size: 0
  }, 
  width: 800, 
  /********
  X-axis Settings
  ********/
  xaxis: {
    anchor: 'y', 
    autorange: false, 
    autotick: true, 
    domain: [0, 1], 
    dtick: 5, 
    exponentformat: 'B', 
    gridcolor: 'rgb(200, 200, 200)', 
    gridwidth: 1, 
    linecolor: '#444', 
    linewidth: 1, 
    mirror: false, 
    nticks: 0, 
    overlaying: false, 
    position: 0, 
    /********
    Set the Range of Years
    ********/
    range: tracePeriod
    //[1969.5, 2012.5]
    , 
    rangemode: 'normal', 
    showexponent: 'all', 
    showgrid: true, 
    showline: false, 
    showticklabels: true, 
    tick0: 0, 
    tickangle: 'auto', 
    tickcolor: '#444', 
    tickfont: {
      color: '', 
      family: '', 
      size: 0
    }, 
    ticklen: 5, 
    ticks: '', 
    tickwidth: 1, 
    title: '', 
    titlefont: {
      color: '', 
      family: '', 
      size: 0
    }, 
    type: 'linear', 
    zeroline: true, 
    zerolinecolor: '#444', 
    zerolinewidth: 1
  }, 
  yaxis: {
    anchor: 'x', 
    autorange: false, 
    autotick: true, 
    domain: [0, 1], 
    dtick: 5, 
    exponentformat: 'B', 
    gridcolor: 'rgb(200, 200, 200)', 
    gridwidth: 1, 
    linecolor: '#444', 
    linewidth: 1, 
    mirror: false, 
    nticks: 0, 
    overlaying: false, 
    position: 0, 
    /********
    Set the Range of Number of Books Published
    *********/
    range: docPublished
    //[-1, 21]
    , 
    rangemode: 'normal', 
    showexponent: 'all', 
    showgrid: true, 
    showline: false, 
    showticklabels: true, 
    tick0: 0, 
    tickangle: 'auto', 
    tickcolor: '#444', 
    tickfont: {
      color: '', 
      family: '', 
      size: 0
    }, 
    ticklen: 5, 
    ticks: '', 
    tickwidth: 1, 
    title: 'Comparison between Conference A and Conference B', 
    titlefont: {
      color: '', 
      family: '', 
      size: 0
    }, 
    type: 'linear', 
    zeroline: true, 
    zerolinecolor: '#444', 
    zerolinewidth: 1
  }
};
Plotly.plot('trend2', {
  data: data,
  layout: layout
});