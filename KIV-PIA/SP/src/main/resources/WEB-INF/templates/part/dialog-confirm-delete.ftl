<div class="modal fade" id="dialog-confirm-delete" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        Warning
      </div>
      <div class="modal-body">
        Are you sure?
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <a id="dialog-confirm-delete-btn-ok" class="btn btn-danger btn-ok">Remove</a>
      </div>
    </div>
  </div>
</div>
<script>
  $(document).ready(function() {
    $('#dialog-confirm-delete').on('shown.bs.modal', function(event) {
      // link z atributu data-href zdrojového <a> se nastaví na tlačítko dialogu
      var link = $(event.relatedTarget).data('href');
      $('#dialog-confirm-delete-btn-ok').attr('href', link);
    });
  });
</script>