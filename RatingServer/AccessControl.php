<?php
use \Luracast\Restler\iAuthenticate;
use \Luracast\Restler\Resources;
class AccessControl implements iAuthenticate
{
    public static $requires = 'user';
    public static $role = 'user';
    public function __isAllowed()
    {
        $roles = array('12345' => 'user', 'SpJNTrgBrpkGS8g3' => 'admin');
        if (!isset($_GET['secret'])
            || !array_key_exists($_GET['secret'], $roles)
        ) {
            return false;
        }
        static::$role = $roles[$_GET['secret']];
        Resources::$accessControlFunction = 'AccessControl::verifyAccess';
        return static::$requires == static::$role || static::$role == 'admin';
    }
    /**
     * @access private
     */
    public static function verifyAccess(array $m)
    {
        $requires =
            isset($m['class']['AccessControl']['properties']['requires'])
                ? $m['class']['AccessControl']['properties']['requires']
                : false;
        return $requires
            ? static::$role == 'admin' || static::$role == $requires
            : true;
    }
}