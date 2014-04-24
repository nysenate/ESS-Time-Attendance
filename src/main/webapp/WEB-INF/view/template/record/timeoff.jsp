<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-container">
    <h1 class="teal">Request Time Off</h1>
    <div style="">
        <p class="content-info">Submit a time-off request for approval by your Time and Attendance supervisor.<br/>
           If the hours are approved you will still have to enter them in time record for that date.</p>

        <div class="timeoff-request-accrual-container">
            <span class="accrual-section-title">Hours available as of April 26, 2014</span>
            <div class="accrual-section">
                <div class="accrual-component">
                    <div class="captioned-hour-square">
                        <div class="hours-caption">Sick</div>
                        <div class="odometer hours-display">232</div>
                    </div>
                </div>
                <div class="accrual-component">
                    <div class="captioned-hour-square">
                        <div class="hours-caption">Personal</div>
                        <div class="hours-display">32</div>
                    </div>
                </div>
                <div class="accrual-component">
                    <div class="captioned-hour-square">
                        <div class="hours-caption">Vacation</div>
                        <div class="hours-display">34</div>
                    </div>
                </div>
            </div>
        </div>

        <form action="">
            <div class="timeoff-request-form-container">
                <div class="label-container">
                    <label>Type</label>
                </div>
                <div class="input-container">
                    <select name="pto-type">
                        <option>Personal</option>
                        <option>Sick</option>
                        <option>Vacation</option>
                        <option>Miscellaneous</option>
                    </select>
                </div>
                <div class="label-container">
                    <label>Duration</label>
                </div>
                <div class="input-container">
                    <div class="radio-button-set">
                        <input id="partial-duration-type-radio" name="duration-type" value="partial" checked="checked" type="radio"/>
                        <label for="partial-duration-type-radio">Partial Day</label>
                        <input id="single-duration-type-radio" name="duration-type" value="single" type="radio"/>
                        <label for="single-duration-type-radio">Single Day</label>
                        <input id="multiple-duration-type-radio" name="duration-type" value="multiple" type="radio"/>
                        <label for="multiple-duration-type-radio">Multiple Days</label>
                    </div>
                </div>
                <div class="label-container">
                    <label for="from-date-input">From Date</label>
                </div>
                <div class="input-container">
                    <input id="from-date-input" class="date-input" type="text" name="from-date" />
                </div>

                <div class="label-container">
                    <label>To Date</label>
                </div>
                <div class="input-container">
                    <input id="to-date-input" class="date-input" type="text" name="to-date"/>
                </div>
            </div>
            <div class="timeoff-request-form-container">
                <div class="label-container">
                    <label for="number-hours-requested">Hours</label>
                </div>
                <div class="input-container">
                    <input type="number" id="number-hours-requested" name="number-hours" value="0"/>
                </div>

                <div class="label-container">
                    <label for="reason-textarea">Reason</label>
                </div>
                <div class="input-container">
                    <textarea id="reason-textarea" draggable="false" name="reason"></textarea>
                </div>
                <div class="label-container"></div>
                <div class="input-container">
                    <input class="submit-button" type="button" value="Submit Time Off Request"/>
                </div>
            </div>
        </form>
        <br/>
    </div>

    <script>
        $('#from-date-input, #to-date-input').datepicker({"dateFormat": "DD, MM d, yy"});
        $(".radio-button-set").buttonset();
    </script>
</section>