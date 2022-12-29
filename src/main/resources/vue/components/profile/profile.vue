<template id="profile">
<h1 class="title">Profile</h1>

  <div class="profile-details">
    <div class="profile">
      <div class="profile-card">
          <label for="name">Name</label>
          <input :disabled="!edit" v-model="profileData.name" required type="text" id="name"/>
          <label for="email">Email</label>
          <input :disabled="!edit" v-model="profileData.email" required type="email" id="email"/>
          <label for="password">Password</label>
          <input :disabled="!edit" v-model="profileData.password" required type="password" id="password"/>
          <button class="btn edit-btn" @click="handleUpdate">
            <span v-if="edit">Save Changes</span>
            <span v-if="!edit">Edit Profile</span>
          </button>
      </div>
    </div>
  </div>

<!--  <div class="modal" id="edit-modal">-->
<!--    <div class="modal-content">-->
<!--      <div class="modal-header">-->
<!--        <span class="close" @click="closeModal('edit-modal')">&times;</span>-->
<!--      </div>-->
<!--      <div class="modal-body">-->
<!--        <div class="add-from" >-->
<!--          <label for="name">Name</label>-->
<!--          <input id="name" type="text" v-model="addActivity.activityName" placeholder="Add Activity Name" required>-->
<!--          <label for="calories">Calories</label>-->
<!--          <input id="calories" type="number" v-model="addActivity.calories" placeholder="Add Calories" required>-->
<!--          <button class="btn submit-btn" @click="editActivity">Update Activity</button>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->

</template>

<script>
// noinspection JSAnnotator
app.component("profile",{
  template:"#profile",
  data:()=>({
    profileData:{},
    edit:false
  }),
  created(){
    fetch('/api/profile').then(res=>res.json()).then(data=>this.profileData = data).catch(err=>console.log(err))
  },
  methods:{
    handleUpdate(){
      if(!this.edit){
        this.edit = true;
      }else{
        fetch('api/profile',{
          method:"patch",
          body:JSON.stringify(this.profileData)
        }).then(res=>res.json()).then((data)=>{this.profileData=data; this.edit=false}).catch((err)=>{console.log(err);alert("Failed to update profile")})
      }
    }
  }
});
</script>

<style>
  .title{
    text-align: center;
    color: goldenrod;
  }
  .profile{
    width:100%;
    display:flex;
    justify-content:center;
    align-items:center;
  }
  .profile label{
    align-self:start;
  }
  .profile .profile-card{
    display:flex;
    flex-direction:column;
    justify-content:center;
    align-items:center;
    padding:50px;
    background-color:#92b6ee;
    border-radius:25px;
    width:50%;
  }
  .profile .profile-card input{
    border:none;
    border-radius: 10px;
    margin:10px 0 10px 0;
    height:30px;
    padding-left:10px;
    width:100%;
  }
  .btn{
    font-size: 14px;
    border: 1px solid;
    border-radius: 5px;
    padding: 4px 6px;
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
</style>