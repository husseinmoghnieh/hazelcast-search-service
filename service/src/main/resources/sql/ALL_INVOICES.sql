SELECT invoice_id,
       invoice_number,
       amount,
       payor.name AS payor_name,
       payee.name AS payee_name
FROM   invoice
       JOIN payor
         ON ( invoice.payor_id = payor.payor_id )
       JOIN payee
         ON ( invoice.payee_id = payee.payee_id )
WHERE  ( invoice.invoice_id IN ( :ids )
          OR :override = 0 )