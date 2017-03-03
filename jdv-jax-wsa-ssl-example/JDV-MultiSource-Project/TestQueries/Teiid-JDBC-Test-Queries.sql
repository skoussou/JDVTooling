    -- WORKS JUST FINE (Needs JDV Connector and WSA-Models VDB Deployed)
  SELECT *
  FROM ADDRESSINGSERVICEVIEW_4.SAYHELLO
  WHERE MESSAGEID = 'UUID-100' AND SAYHELLO = 'GOTCHA' 
  AND ADDRESSINGSERVICEVIEW_4.SAYHELLO.To = 'http://www.w3.org/2005/08/addressing/anonymous'
  AND ADDRESSINGSERVICEVIEW_4.SAYHELLO.ReplyTo = 'http://www.w3.org/2005/08/addressing/anonymous'
  AND ADDRESSINGSERVICEVIEW_4.SAYHELLO.Action = 'http://www.w3.org/2005/08/addressing/ServiceIface/sayHello'
  
