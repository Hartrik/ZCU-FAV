<form class="form-vertical" action="${form_user_details_action}" method="POST" data-toggle="validator">
  <fieldset>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div class="form-row col-md-12">
      <div class="form-group col-md-6">
        <label for="firstName">First name</label>
        <input id="firstName" name="firstName" class="form-control"
               type="text" maxlength="20" required
               value="<#if default??>${default.firstName!''}</#if>">
      </div>
      <div class="form-group col-md-6">
        <label for="lastName">Last name</label>
        <input id="lastName" name="lastName" class="form-control"
               type="text" maxlength="20" required
               value="<#if default??>${default.lastName!''}</#if>">
      </div>
    </div>

    <div class="form-row col-md-12">
      <div class="form-group col-md-6">
        <label for="personalNumber">Personal number</label>
        <input id="personalNumber" name="personalNumber" class="form-control"
               type="text" maxlength="15" required
               value="<#if default??>${default.personalNumber!''}</#if>">
      </div>
    </div>

    <div class="form-row col-md-12">
      <div class="form-group col-md-6">
        <label for="email">E-mail</label>
        <input id="email" name="email" class="form-control"
               type="email" maxlength="64" required
               value="<#if default??>${default.email!''}</#if>">
      </div>
    </div>

    <script>
      // disable spaces in form inputs
      $("#firstName,#lastName,#personalNumber,#email").on({
        keydown: function(e) {
          if (e.which === 32)
            return false;
        },
        change: function() {
          this.value = this.value.replace(/\s/g, "");
        }
      });
    </script>

    <#if form_user_details_new??>
      <input type="hidden" id="turingTestQuestionId" name="turingTestQuestionId" value="${turing_test.id}">
      <div class="form-row col-md-12">
        <div class="form-group col-md-12">
          <label for="turingTestAnswer">Anti-Robot Test: ${turing_test.question}</label>
          <input id="turingTestAnswer" name="turingTestAnswer" type="text" class="form-control"
                 required>
        </div>
      </div>

      <div class="form-row col-md-12">
        <div class="form-group col-md-12">
          <div class="checkbox">
            <label>
              <input type="checkbox" id="terms" required>
              <strong>Customer accepted the <u>Terms and Conditions</u></strong>
            </label>
          </div>
        </div>
      </div>
    </#if>

    <div class="form-row col-md-12">
      <div class="form-group col-md-12 text-center">
        <button id="apply" class="btn btn-primary">Submit</button>
      </div>
    </div>

  </fieldset>
</form>