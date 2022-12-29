<template id="hello-world">
  <header>
    <h1 class="hello-world">Health Tracker</h1>
    <div>
      <form
        method="get"
        action="/api/users/logout"
      >
      <button class="btn logout-btn">Logout</button>
      </form>
      <a class="btn link-btn" href="/profile">Profile</a>
      <button class="btn add-btn" @click="showModal('modal')">Add Activity</button>
    </div>
  </header>

  <section class="activtity-header">
    <h3 class="title">Activities</h3>
  </section>

  <section class="table-container">
    <table>
      <tr>
          <th>Id</th>
          <th>Name</th>
          <th>Calories</th>
          <th>Date / Time</th>
          <th>Actions</th>
      </tr>
      <tr v-for="activity in activities" :key="activity.id">
          <td>{{activity.id}}</td>
          <td>{{activity.activityName}}</td>
          <td>{{activity.calories}}</td>
          <td>{{new Date(activity.createdAt).toDateString()}}</td>
          <td class="action-container">
            <button class="btn edit-btn" @click="showEditModal('general',activity.id)">Edit</button>
            <button class="btn delete-btn" @click="deleteActivity('general',activity.id)">Delete</button>
          </td>
      </tr>
    </table>
  </section>

  <section class="activtity-header">
    <h3 class="title">Blood Pressure Activities</h3>
  </section>
  <section class="table-container">
    <table>
      <tr>
        <th>Id</th>
        <th>Systolic</th>
        <th>Diastolic</th>
        <th>Date / Time</th>
        <th>Actions</th>
      </tr>
      <tr v-for="activity in bloodPressureActivities" :key="activity.id">
        <td>{{activity.id}}</td>
        <td>{{activity.systolic}}</td>
        <td>{{activity.diastolic}}</td>
        <td>{{new Date(activity.createdAt).toDateString()}}</td>
        <td class="action-container">
          <button class="btn edit-btn" @click="showEditModal('blood_pressure',activity.id)">Edit</button>
          <button class="btn delete-btn" @click="deleteActivity('blood_pressure',activity.id)">Delete</button>
        </td>
      </tr>
    </table>
  </section>

  <section class="activtity-header">
    <h3 class="title">Running Activities</h3>
  </section>
  <section class="table-container">
    <table>
      <tr>
        <th>Id</th>
        <th>Distance</th>
        <th>Calories</th>
        <th>Date / Time</th>
        <th>Actions</th>
      </tr>
      <tr v-for="activity in runningActivities" :key="activity.id">
        <td>{{activity.id}}</td>
        <td>{{activity.distance}}</td>
        <td>{{activity.calories}}</td>
        <td>{{new Date(activity.createdAt).toDateString()}}</td>
        <td class="action-container">
          <button class="btn edit-btn" @click="showEditModal('running',activity.id)">Edit</button>
          <button class="btn delete-btn" @click="deleteActivity('running',activity.id)">Delete</button>
        </td>
      </tr>
    </table>
  </section>

  <section class="activtity-header">
    <h3 class="title">Temperature Activities</h3>
  </section>
  <section class="table-container">
    <table>
      <tr>
        <th>Id</th>
        <th>Temperature</th>
        <th>Date / Time</th>
        <th>Actions</th>
      </tr>
      <tr v-for="activity in temperatureActivities" :key="activity.id">
        <td>{{activity.id}}</td>
        <td>{{activity.temperature}}</td>
        <td>{{new Date(activity.createdAt).toDateString()}}</td>
        <td class="action-container">
          <button class="btn edit-btn" @click="showEditModal('temperature',activity.id)">Edit</button>
          <button class="btn delete-btn" @click="deleteActivity('temperature',activity.id)">Delete</button>
        </td>
      </tr>
    </table>
  </section>

  <div class="modal" id="modal">
    <div class="modal-content">
      <div class="modal-header">
      <span class="close" @click="closeModal('modal')">&times;</span>
      </div>
      <div class="modal-body">
        <div class="add-from" >
          <label for="activity_type">Activity Type</label>
          <select id="activity_type" v-model="addActivity.activity_type" default="blood_pressure">
            <option value="blood_pressure">Blood Pressure</option>
            <option value="running">Running</option>
            <option value="temperature">Temperature</option>
            <option value="general">General Activity</option>
          </select>

<!--          Blood Pressure Activity-->
          <div v-if="addActivity.activity_type === 'blood_pressure'">
            <label for="systolic">Systolic</label>
            <input id="systolic" type="number" v-model="addActivity.systolic" placeholder="Systolic value" required/>
            <label for="diastolic">Diastolic</label>
            <input id="diastolic" type="number" v-model="addActivity.diastolic" placeholder="Diastolic value" required/>
          </div>

<!--          Running Activity-->
          <div v-if="addActivity.activity_type === 'running'">
            <label for="distance">Distance</label>
            <input id="distance" type="number" v-model="addActivity.distance" placeholder="Distance value" required/>
            <label for="calories">Calories</label>
            <input id="calories" type="number" v-model="addActivity.calories" placeholder="Calories value" required/>
          </div>

<!--          Temperature Activity-->
          <div v-if="addActivity.activity_type === 'temperature'">
            <label for="temperature">Temperature</label>
            <input id="temperature" type="number" v-model="addActivity.temperature" placeholder="Temperature value" required/>
          </div>

          <div v-if="addActivity.activity_type === 'general'">
            <label for="name">Name</label>
            <input id="name" type="text" v-model="addActivity.name" placeholder="Name" required>
            <label for="calories">Calories</label>
            <input id="calories" type="number" v-model="addActivity.calories" placeholder="Add Calories" required>
          </div>
          <label for="createdAt">Date and time</label>
          <input id="createdAt" type="datetime-local" v-model="addActivity.createdAt" placeholder="add date and time" required/>
          <button class="btn submit-btn" @click="createActivity" :disabled="!addActivity.activity_type">Add Activity</button>
        </div>
      </div>
    </div>
  </div>

  <div class="modal" id="edit-modal">
    <div class="modal-content">
      <div class="modal-header">
        <span class="close" @click="closeModal('edit-modal')">&times;</span>
      </div>
      <div class="modal-body">
        <div class="add-from" >
          <label for="activity_type">Activity Type</label>
          <select id="activity_type" v-model="addActivity.activity_type" default="blood_pressure" disabled>
            <option value="blood_pressure">Blood Pressure</option>
            <option value="running">Running</option>
            <option value="temperature">Temperature</option>
            <option value="general">General Activity</option>
          </select>

          <!--          Blood Pressure Activity-->
          <div v-if="addActivity.activity_type === 'blood_pressure'">
            <label for="systolic">Systolic</label>
            <input id="systolic" type="number" v-model="addActivity.systolic" placeholder="Systolic value" required/>
            <label for="diastolic">Diastolic</label>
            <input id="diastolic" type="number" v-model="addActivity.diastolic" placeholder="Diastolic value" required/>
          </div>

          <!--          Running Activity-->
          <div v-if="addActivity.activity_type === 'running'">
            <label for="distance">Distance</label>
            <input id="distance" type="number" v-model="addActivity.distance" placeholder="Distance value" required/>
            <label for="calories">Calories</label>
            <input id="calories" type="number" v-model="addActivity.calories" placeholder="Calories value" required/>
          </div>

          <!--          Temperature Activity-->
          <div v-if="addActivity.activity_type === 'temperature'">
            <label for="temperature">Temperature</label>
            <input id="temperature" type="number" v-model="addActivity.temperature" placeholder="Temperature value" required/>
          </div>

          <div v-if="addActivity.activity_type === 'general'">
            <label for="name">Name</label>
            <input id="name" type="text" v-model="addActivity.name" placeholder="Name" required>
            <label for="calories">Calories</label>
            <input id="calories" type="number" v-model="addActivity.calories" placeholder="Add Calories" required>
          </div>
          <label for="createdAt">Date and time</label>
          <input id="createdAt" type="datetime-local" v-model="addActivity.createdAt" placeholder="add date and time" required/>
          <button class="btn submit-btn" @click="editActivity">Update Activity</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  // noinspection JSAnnotator
  app.component("hello-world", {
    template: "#hello-world",
     data: () => ({
        users: null,
        activities:null,
        addActivity:{
        },
       bloodPressureActivities:[],
       runningActivities:[],
       temperatureActivities:[],
     }),
     created() {
         fetch(`/api/activities`)
            .then(res => res.json())
            .then(json => {
                this.activities = json
            })
            .catch(() => alert("Error while fetching activities"));
       fetch(`/api/blood-pressure`)
           .then(res => res.json())
           .then(json => {
             this.bloodPressureActivities = json
           })
           .catch(() => alert("Error while fetching activities"));
       fetch(`/api/running`)
           .then(res => res.json())
           .then(json => {
             this.runningActivities = json
           })
           .catch(() => alert("Error while fetching activities"));
       fetch(`/api/temperature`)
           .then(res => res.json())
           .then(json => {
             this.temperatureActivities = json
           })
           .catch(() => alert("Error while fetching activities"));
     },
    methods:{
       async deleteActivity(activityType,id){
         switch (activityType) {
           case "general":
             this.makeHttpRequest(`/api/activities/${id}`, "DELETE")
             this.activities = this.activities.filter(item => item.id !== id);
             return;
           case "blood_pressure":
             this.makeHttpRequest(`api/blood-pressure/${id}`,"DELETE");
             this.bloodPressureActivities = this.bloodPressureActivities.filter(item=>item.id!==id);
             return;
           case "temperature":
             this.makeHttpRequest(`/api/temperature/${id}`,"DELETE");
             this.temperatureActivities = this.temperatureActivities.filter(item=>item.id!==id);
             return;
           case "running":
             this.makeHttpRequest(`/api/running/${id}`,"DELETE");
             this.runningActivities = this.runningActivities.filter(item=>item.id!==id);
           default:
             return;
         }

      },


      async makeHttpRequest(uri,method,body){
         try {
           const data = await fetch(uri, {
             method: method,
             body: body
           }).then(res => res.json()).then(item => item)
           return data;
         }catch(err){
           console.log(err);
         }
      },

      async createActivity(){
        try{
          console.log(this.addActivity)

          switch(this.addActivity.activity_type){
            case "blood_pressure": {
              const data = JSON.stringify({
                systolic: this.addActivity.systolic,
                diastolic: this.addActivity.diastolic,
                createdAt: this.addActivity.createdAt,
              })
              const result = await this.makeHttpRequest('/api/blood-pressure', "POST", data)
              this.bloodPressureActivities.push(result);
              this.addActivity={}
              this.closeModal("modal")
              return;
            }
            case "running":{
              const data = JSON.stringify({
                distance: this.addActivity.distance,
                calories: this.addActivity.calories,
                createdAt: this.addActivity.createdAt
              })
              const result = await this.makeHttpRequest('/api/running',"POST", data)
              this.runningActivities.push(result)
              this.addActivity={}
              this.closeModal("modal")
                return;
              }
            case "temperature": {
              const data = JSON.stringify({
                temperature: this.addActivity.temperature,
                createdAt: this.addActivity.createdAt
              })
              const result = await this.makeHttpRequest('/api/temperature',"POST",data);
              this.temperatureActivities.push(result);
              this.addActivity={}
              this.closeModal("modal")
              return;

            }
            case "cholestrol": {

            }
            case "general": {
              const data = JSON.stringify({
                calories: this.addActivity.calories,
                activityName: this.addActivity.name,
                createdAt: this.addActivity.createdAt
              })
              const result = await this.makeHttpRequest('/api/activities', "POST", data);
              this.activities.push(result)
              this.addActivity={}
              this.closeModal("modal");
              return;
            }
            default:

          }

        } catch (err){
          console.log(err)
          console.log(err)
          alert("Failed to add new activity")
        }
      },

      showEditModal(activity_type,activityId){
         switch (activity_type){
           case "blood_pressure": {
            this.addActivity=Object.assign({},{...this.bloodPressureActivities.find(({id})=>id===activityId), activity_type})
             if(this.addActivity.id){
               this.showModal("edit-modal")
             }
             return;
           }
           case "running":{
             this.addActivity=Object.assign({},{...this.runningActivities.find(({id})=>id===activityId), activity_type})
             if(this.addActivity.id){
               this.showModal("edit-modal")
             }
             return;
           }
           case "temperature": {
             this.addActivity=Object.assign({},{...this.temperatureActivities.find(({id})=>id===activityId), activity_type})
             if(this.addActivity.id){
               this.showModal("edit-modal")
             }
             return;

           }
           case "cholestrol": {

           }
           case "general": {
             console.log(this.activities.find(({id})=>id===activityId))
             const activity = this.activities.find(({id})=>id===activityId)
             this.addActivity=Object.assign({},{...activity, name:activity.activityName, activity_type,createdAt: new Date(activity.createdAt).toISOString()})
             if(this.addActivity.id){
               this.showModal("edit-modal")
             }
             return;
           }
           default:
         }

      },

      async editActivity(){
        try{
          switch(this.addActivity.activity_type){
            case "blood_pressure": {
              const data = JSON.stringify({
                systolic: this.addActivity.systolic,
                diastolic: this.addActivity.diastolic,
                createdAt: this.addActivity.createdAt,
              })
              const result = await this.makeHttpRequest(`/api/blood-pressure/${this.addActivity.id}`, "PATCH", data)
              this.bloodPressureActivities = this.bloodPressureActivities.map(item=>item.id===result.id?result:item);
              this.addActivity={}
              this.closeModal("edit-modal")
              return;
            }
            case "running":{
              const data = JSON.stringify({
                distance: this.addActivity.distance,
                calories: this.addActivity.calories,
                createdAt: this.addActivity.createdAt
              })
              const result = await this.makeHttpRequest(`/api/running/${this.addActivity.id}`,"PATCH", data)
              this.runningActivities = this.runningActivities.map(item=>item.id===result.id?result:item)
              this.addActivity={}
              this.closeModal("edit-modal")
              return;
            }
            case "temperature": {
              const data = JSON.stringify({
                temperature: this.addActivity.temperature,
                createdAt: this.addActivity.createdAt
              })
              const result = await this.makeHttpRequest(`/api/temperature/${this.addActivity.id}`,"PATCH",data);
              this.temperatureActivities = this.temperatureActivities.push(item=>item.id===result.id?result:item);
              this.addActivity={}
              this.closeModal("edit-modal")
              return;

            }
            case "cholestrol": {

            }
            case "general": {
              const data = JSON.stringify({
                calories: this.addActivity.calories,
                activityName: this.addActivity.name,
                createdAt: this.addActivity.createdAt
              })
              const result = await this.makeHttpRequest(`/api/activities/${this.addActivity.id}`, "PATCH", data);
              this.activities = this.activities.map(item=>item.id===result.id?result:item)
              this.addActivity={}
              this.closeModal("edit-modal");
              return;
            }
            default:

          }


         }catch (err){
           console.log(err);
           alert("Failed to update activity")
         }
      },

      showModal(modalId){
         const modal = document.getElementById(modalId);
         modal.style.display = "block";
      },

      closeModal(modalId){
         const modal = document.getElementById(modalId);
         modal.style.display = "none";
      }
    }
   });
</script>
<style>
  header{
    display:flex;
    flex-direction:row;
    align-items:center;
    justify-content:center;
  }
  .hello-world {
    color: goldenrod;
    text-align:center;
    margin-left:auto;
    margin-right:auto;
  }
  .title{
    color: teal;
    text-align:center;
  }
  table, th, td {
    border:1px solid black;
    color: black;
    text-align: center;
    min-width: fit-content;
  }
  table{
    width:100%;
  }

  .action-container button{
    margin-right: 5px;
  }

  .logout-btn{
    background-color: tomato;
    color:white;
    border-color: tomato;
  }
  .logout-btn:hover{
    color: tomato;
    background-color: white;
  }
  .activtity-header{
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .activtity-header .title{
    flex:2;
  }

  .btn{
    font-size: 14px;
    border: 1px solid;
    border-radius: 5px;
    padding: 4px 6px;
  }

  .add-btn{
    border-color:#3478e3;
    background-color: #3478e3;
    color: white;
  }
  .add-btn:hover{
    background-color: white;
    color: #3478e3;
  }

  .edit-btn{
    border-color:#17a2b8;
    background-color: #17a2b8;
    color: white;
  }
  .edit-btn:hover{
    color:#17a2b8;
    background-color: white;
  }

  .delete-btn{
    border-color:tomato;
    background-color: tomato;
    color:white;
  }
  .delete-btn:hover{
    color: tomato;
    background-color: white;
  }

  .add-from{
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .add-from input,select{
    border:none;
    border-radius: 5px;
    margin:10px 0 10px 0;
    height:30px;
    padding-left:10px;
    width:100%;
  }

  .submit-btn{
    border-color: darkgreen;
    background-color: darkgreen;
    color: white;
  }

  .submit-btn:hover{
    background-color: white;
    color: darkgreen;
  }

  .modal {
    display: none; /* Hidden by default */
    position: fixed; /* Stay in place */
    z-index: 1; /* Sit on top */
    left: 0;
    top: 0;
    width: 100%; /* Full width */
    height: 100%; /* Full height */
    overflow: auto; /* Enable scroll if needed */
    background-color: rgb(0,0,0); /* Fallback color */
    background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
  }

  /* Modal Content/Box */
  .modal-content {
    background-color: #d1d1d1;
    margin: 15% auto; /* 15% from the top and centered */
    padding: 20px;
    border: 1px solid #888;
    width: max-content; /* Could be more or less, depending on screen size */
    color: black;
    display: flex;
    flex: 1;
    flex-direction: column;
  }

  /* The Close Button */
  .close {
    color: black;
    font-size: 28px;
    font-weight: bold;
  }

  .close:hover,
  .close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
  }
  .link-btn{
    color:white;
    background-color: lightblue;
  }
  .link-btn:hover{
    color:lightblue;
    background-color: white;
  }

  .table-container{
    max-height: 50vh;
    overflow-y: scroll;
  }

</style>