<template id="hello-world">
  <header>
  <h1 class="hello-world">Health Tracker</h1>
  <form
    method="get"
    action="/api/users/logout"
  >
  <button class="btn logout-btn">Logout</button>
  </form>
  </header>

  <section class="activtity-header">
  <h3 class="title">Activities</h3>
    <button class="btn add-btn" @click="showModal('modal')">Add Activity</button>
  </section>
    <table>
      <tr>
          <th>Id</th>
          <th>Name</th>
          <th>Calories</th>
          <th>Actions</th>
      </tr>
      <tr v-for="activity in activities" :key="activity.id">
          <td>{{activity.id}}</td>
          <td>{{activity.activityName}}</td>
          <td>{{activity.calories}}</td>
          <td class="action-container">
            <button class="btn edit-btn" @click="showEditModal(activity.id)">Edit</button>
            <button class="btn delete-btn" @click="deleteActivity(activity.id)">Delete</button>
          </td>
      </tr>
    </table>

  <div class="modal" id="modal">
    <div class="modal-content">
      <div class="modal-header">
      <span class="close" @click="closeModal('modal')">&times;</span>
      </div>
      <div class="modal-body">
        <div class="add-from" >
          <label for="name">Name</label>
          <input id="name" type="text" v-model="addActivity.name" placeholder="Add Activity Name" required>
          <label for="calories">Calories</label>
          <input id="calories" type="number" v-model="addActivity.calories" placeholder="Add Calories" required>
          <button class="btn submit-btn" @click="createActivity">Add Activity</button>
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
          <label for="name">Name</label>
          <input id="name" type="text" v-model="addActivity.activityName" placeholder="Add Activity Name" required>
          <label for="calories">Calories</label>
          <input id="calories" type="number" v-model="addActivity.calories" placeholder="Add Calories" required>
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
          name:'',
          calories:null
        }
     }),
     created() {
         fetch(`/api/activities`)
            .then(res => res.json())
            .then(json => {
                console.log(json)
                this.activities = json
            })
            .catch(() => alert("Error while fetching activities"));
     },
    methods:{
       async deleteActivity(id){
          try{
            await fetch(`api/activities/${id}`,{
              method:"delete"
            })
            this.activities = this.activities.filter(activity=>activity.id !== id)
          }catch(err){
            alert("Failed to delete activity")
          }
      },

      async createActivity(){
        try{
          console.log(this.addActivity)
          const data = await fetch('api/activities',{
            method:"post",
            body:JSON.stringify({
              activityName:this.addActivity.name,
              calories: this.addActivity.calories,
            }),
          }).then(res=>res.json()).then(item=>item)
          console.log(data);
          this.activities.push(data)
          this.addActivity.calories=null
          this.addActivity.name=null
          this.closeModal("modal")
        } catch (err){
          console.log(err)
          console.log(err)
          alert("Failed to add new activity")
        }
      },

      showEditModal(activityId){
        this.addActivity = Object.assign({},this.activities.find(({id})=>id === activityId))
        if(this.addActivity.id){
          this.showModal("edit-modal")
        }
      },

      async editActivity(){
         try{
          const data = await fetch(`/api/activities/${this.addActivity.id}`,{
            body:JSON.stringify(this.addActivity),
            method:"patch"
          }).then(res=>res.json());
          this.activities = this.activities.map(activity=>activity.id === data.id?data:activity)
           this.closeModal("edit-modal")
           this.addActivity = {}
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
  body{
    background-color: rgba(0,0,0,0.9);
    color: white;
  }
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
    border:1px solid white;
    color: whitesmoke;
    text-align: center;
    width:25%;
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
  .add-from input{
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

</style>